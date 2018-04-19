package org.plcore.dao.berkeley;

import java.lang.reflect.Field;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.plcore.dao.IDataAccessObject;
import org.plcore.osgi.Configurable;
import org.plcore.osgi.ConfigurationLoader;
import org.plcore.value.EntityLife;
import org.plcore.value.VersionTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Transaction;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.model.PrimaryKey;


@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class DataAccessObject<T> implements IDataAccessObject<T> {

  private final Logger logger = LoggerFactory.getLogger(DataAccessObject.class);
  
  @Reference(name = "store")
  private DataStore dataStore;
  
  @Reference
  private ConfigurationLoader configLoader;

  @Configurable(name = "class", required = true)
  private Class<T> entityClass;
  
  private Field idField;
  private Field versionTimeField;
  private Field entityLifeField;
  
  private PrimaryIndex<Object, T> primaryIndex = null;
  private Map<String, SecondaryIndex<Object, Object, T>> secondaryIndexes = new HashMap<>();
  
  
  private Field getDeclaredField(String name) {
    try {
      return entityClass.getDeclaredField(name);
    } catch (NoSuchFieldException | SecurityException ex) {
      return null;
    }
  }

  
  @Activate
  private void activate (ComponentContext context) {
    configLoader.load(this, context);
    
    idField = null;
    entityClass.getDeclaredFields();
    for (Field field : entityClass.getDeclaredFields()) {
      if (field.isAnnotationPresent(PrimaryKey.class)) {
        idField = field;
        break;
      }
    }
    versionTimeField = getDeclaredField("versionTime");
    entityLifeField = getDeclaredField("entityLife");
    
    primaryIndex = null;
  }

  
  @SuppressWarnings("unchecked")
  private PrimaryIndex<Object, T> getPrimaryIndex(Class<?> keyClass) {
    if (primaryIndex == null) {
      primaryIndex = dataStore.getPrimaryIndex((Class<Object>)keyClass, entityClass);      
    }
    return primaryIndex;
  }
  
  
  @SuppressWarnings("unchecked")
  private SecondaryIndex<Object, Object, T> getSecondaryIndex(String name, Class<?> keyClass) {
    if (primaryIndex == null) {
      primaryIndex = dataStore.getPrimaryIndex(Object.class, entityClass);      
    }
    SecondaryIndex<Object, Object, T> index2 = secondaryIndexes.get(name);
    if (index2 == null) {
      index2 = dataStore.getSecondaryIndex(primaryIndex, (Class<Object>)keyClass, name);
      secondaryIndexes.put(name, index2);
    }
    return index2;
  }
  
  
  @Override
  public T add(T value) {
    try {
      idField.setAccessible(true);
      
      if (versionTimeField != null) {
        VersionTime versionTime = VersionTime.now();
        versionTimeField.setAccessible(true);
        versionTimeField.set(value, versionTime);
      }
      
      if (entityLifeField != null) {
        entityLifeField.setAccessible(true);
        entityLifeField.set(value, EntityLife.ACTIVE);
      }
    } catch (SecurityException | IllegalArgumentException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
    
    // Put it in the store.
    Transaction transaction = dataStore.beginTransaction();
    try {
      PrimaryIndex<Object, T> index1 = getPrimaryIndex(idField.getType());
      index1.put(value);
      transaction.commit();
      logger.info("add: " + value);
    } catch (Exception ex) {
      transaction.abort();
    }
    return value;
  }

  
  @Override
  public void close() {
    // Nothing to do for Berkeley database
  }

  
  @Override
  public T getById(int id) {
    PrimaryIndex<Object, T> index1 = getPrimaryIndex(Integer.class);
    T value = index1.get(id);
    logger.info("getById " + id + ": " + value);
    return value;
  }
  

  @Override
  public T getByPrimary(Object key) {
    PrimaryIndex<Object, T> index1 = getPrimaryIndex(key.getClass());
    T value = index1.get(key);
    logger.info("getById " + key + ": " + value);
    return value;
  }
  

  @Override
  public T getByIndex(String indexName, Object key) {
    SecondaryIndex<Object, Object, T> index2 = getSecondaryIndex(indexName, key.getClass());
    T value = index2.get(key);
    return value;
  }
  
  
  @Override
  public void remove(T value) throws ConcurrentModificationException {
    Transaction transaction = dataStore.beginTransaction();
    try {
      idField.setAccessible(true);
      Object key = idField.get(value);
      
      T value2 = primaryIndex.get(key);

      if (versionTimeField != null) {
        versionTimeField.setAccessible(true);
        VersionTime oldTime = (VersionTime)versionTimeField.get(value);
        VersionTime newTime = (VersionTime)versionTimeField.get(value2);
        if (!oldTime.equals(newTime)) {
          throw new ConcurrentModificationException(oldTime + " vs " + newTime);
        }
      }
      
      PrimaryIndex<Object, T> index1 = getPrimaryIndex(idField.getType());
      index1.delete(key);
      logger.info("delete " + key + ": " + value2);
      transaction.commit();
    } catch (SecurityException | IllegalArgumentException |
             IllegalAccessException | DatabaseException ex) {
      transaction.abort();
      throw new RuntimeException(ex);
    }
  }

  
  /**
   * Change an entity.  Only the 'id' and 'versionTime' of the old value is used.
   */
  @Override
  public T update(T newValue) throws ConcurrentModificationException {
    Transaction transaction = dataStore.beginTransaction();
    try {
      idField.setAccessible(true);
      Object key = idField.getInt(newValue);

      Object value = primaryIndex.get(key);
      if (versionTimeField != null) {
        versionTimeField.setAccessible(true);
        VersionTime oldTime = (VersionTime)versionTimeField.get(value);
        VersionTime newTime = (VersionTime)versionTimeField.get(newValue);
        if (!oldTime.equals(newTime)) {
          throw new ConcurrentModificationException(oldTime + " vs " + newTime);
        }
        newTime = VersionTime.now();
        versionTimeField.set(newValue, newTime);
      }
      
      PrimaryIndex<Object, T> index1 = getPrimaryIndex(idField.getType());
      index1.put(newValue);
      transaction.commit();
      logger.info("update " + key + ": " + newValue);
      return newValue;
    } catch (SecurityException | IllegalArgumentException |
             IllegalAccessException | DatabaseException ex) {
      transaction.abort();
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override
  public void getAll (Consumer<T> consumer) {
    PrimaryIndex<Object, T> index1 = getPrimaryIndex(idField.getType());
    EntityCursor<T> cursor = index1.entities();
    try {
      for (T value : cursor) {
        consumer.accept(value);
      }
    } finally {
      cursor.close();
    }
  }
  
}
