package org.plcore.dao.berkeley;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.plcore.osgi.Configurable;
import org.plcore.osgi.ConfigurationLoader;

import com.sleepycat.je.Transaction;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.model.AnnotationModel;
import com.sleepycat.persist.model.EntityModel;


@Component(service = DataStore.class, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class DataStore {

  @Reference
  private ConfigurationLoader configLoader;
  
  @Reference(name = "environment")
  private DataEnvironment environment;
  
  @Configurable(required = true)
  private String name;
  
  private EntityStore store;
  

  @Activate 
  public void activate (ComponentContext componentContext) {
    configLoader.load(this, componentContext);
    
    EntityModel model = new AnnotationModel();
    model.registerClass(LocalDateProxy.class);
    model.registerClass(DecimalProxy.class);
    model.registerClass(CRC64DigestProxy.class);
    model.registerClass(VersionTimeProxy.class);
    model.registerClass(MimeTypeProxy.class);

    StoreConfig storeConfig = new StoreConfig();
    storeConfig.setAllowCreate(true);
    storeConfig.setModel(model);
    storeConfig.setTransactional(true);
    
    // Open the entity store
    store = environment.newEntityStore(name, storeConfig);
  }
  
  
  // TODO remove
  public EntityStore getEntityStore() {
    return store;
  }
  
  
  @Deactivate
  public void deactivate () {
    store.close();
  }
  
  
  public <PK,E> PrimaryIndex<PK,E> getPrimaryIndex (Class<PK> pkClass, Class<E> entityClass) {
    return store.getPrimaryIndex(pkClass, entityClass);
  }
  

  public <SK,PK,E> SecondaryIndex<SK,PK,E> getSecondaryIndex (PrimaryIndex<PK,E> primaryIndex, Class<SK> skClass, String name) {
    return store.getSecondaryIndex(primaryIndex, skClass, name);
  }
  
  
  public <E> SecondaryIndex<String,Integer,E> getSecondaryIndex (PrimaryIndex<Integer,E> primaryIndex, String name) {
    return getSecondaryIndex(primaryIndex, String.class, name);
  }

  
  public Transaction beginTransaction() {
    return store.getEnvironment().beginTransaction(null, null);
  }
  
}
