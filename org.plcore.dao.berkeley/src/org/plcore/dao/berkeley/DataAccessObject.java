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
import org.plcore.dao.ITransaction;
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
  private boolean idSequence;
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
    idSequence = false;
    entityClass.getDeclaredFields();
    for (Field field : entityClass.getDeclaredFields()) {
      PrimaryKey pkAnn = field.getAnnotation(PrimaryKey.class);
      if (pkAnn != null) {
        idField = field;
        idSequence = pkAnn.sequence().length() > 0;
        break;
      }
    }
    versionTimeField = getDeclaredField("versionTime");
    entityLifeField = getDeclaredField("entityLife");
    
    primaryIndex = null;
  }

  
  @SuppressWarnings("unchecked")
  private PrimaryIndex<Object, T> getPrimaryIndex() {
    Class<?> keyClass = null;
    
    if (primaryIndex == null) {
      Field[] fields = entityClass.getDeclaredFields();
      for (Field field : fields) {
        if (field.isAnnotationPresent(PrimaryKey.class)) {
          keyClass = field.getType();
          if (keyClass == int.class) {
            keyClass = Integer.class;
          } else if (keyClass == long.class) {
            keyClass = Long.class;
          }
          break;
        }
      }
      if (keyClass == null) {
        throw new RuntimeException("No @PrimaryKey field in " + entityClass);
      }
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
    Transaction transaction = dataStore.beginTransaction();
    try {
      PrimaryIndex<Object, T> index1 = getPrimaryIndex();
      add(index1, value);
      transaction.commit();
      logger.info("add: " + value);
    } catch (Exception ex) {
      transaction.abort();
    }
    return value;
  }

  
  T add(PrimaryIndex<Object, T> index, T value) {
    try {
      if (idSequence) {
        idField.setAccessible(true);
        idField.set(value, 0);
      }
      
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
    
    index.put(value);
    return value;
  }

  
  @Override
  public ITransaction<T> getTransaction() {
    PrimaryIndex<Object, T> index1 = getPrimaryIndex();
    index1 = getPrimaryIndex();
    return new TransactionSet<>(this, dataStore.beginTransaction(), index1);
  }
  
  
  @Override
  public void close() {
    // Nothing to do for Berkeley database
  }

  
  @Override
  public T getById(int id) {
    PrimaryIndex<Object, T> index1 = getPrimaryIndex();
    T value = index1.get(id);
    logger.info("getById " + id + ": " + value);
    return value;
  }
  

  @Override
  public T getByPrimary(Object key) {
    PrimaryIndex<Object, T> index1 = getPrimaryIndex();
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
      PrimaryIndex<Object, T> index = getPrimaryIndex();
      remove(index, value);
      logger.info("delete " + value);
      transaction.commit();
    } catch (SecurityException | IllegalArgumentException |
             DatabaseException ex) {
      transaction.abort();
      throw new RuntimeException(ex);
    }
  }

  
  void remove(PrimaryIndex<Object, T> index, T value) throws ConcurrentModificationException {
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
      index.delete(key);
    } catch (SecurityException | IllegalArgumentException |
             IllegalAccessException | DatabaseException ex) {
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
      Object key = idField.get(newValue);

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
      
      PrimaryIndex<Object, T> index1 = getPrimaryIndex();
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
    PrimaryIndex<Object, T> index1 = getPrimaryIndex();
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
