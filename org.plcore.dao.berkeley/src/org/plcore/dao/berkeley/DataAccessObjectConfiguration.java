package org.plcore.dao.berkeley;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.plcore.entity.IEntity;
import org.plcore.osgi.DynamicConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = DataAccessObjectConfiguration.class, immediate = true)
public class DataAccessObjectConfiguration {

  /**
   * The name of what we are configuring. In this case, a DataAccessObject
   */
  private static final String CONFIG_NAME = DataAccessObject.class.getName();
  
  private final Logger logger = LoggerFactory.getLogger(DataAccessObjectConfiguration.class);
  
  @Reference
  private DynamicConfigurer dynamicConfigurer;
  
  private boolean activated = false;
  
  private List<IEntity> deferred = new LinkedList<>();
  
  
  @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
  private void addCandidate(IEntity candidate) {
    if (activated) {
      register(candidate);
    } else {
      logger.info("IDataAccessObject for {}, registration deferred", candidate.getClass().getName());
      deferred.add(candidate);
    }
  }
  
  
  @SuppressWarnings("unused")
  private void removeCandidate(IEntity candidate) {
    unregister(candidate);
  }
  
  
  private void register(IEntity candidate) {
    String name = candidate.getClass().getSimpleName();
    
    Dictionary<String, Object> props = new Hashtable<>();
    props.put("name", name);
    
    String className = candidate.getClass().getName();
    if (className.startsWith("org.plcore.")) {
      props.put("store.target", "(name=CoreDataStore)");
    }
    props.put("class", className);
    logger.info(props.toString());

    dynamicConfigurer.setConfig(CONFIG_NAME, name, props);
    logger.info("IDataAccessObject for {} is registered", candidate.getClass().getName());
  }
  
  
  private void unregister(IEntity candidate) {
    String name = candidate.getClass().getSimpleName();

    dynamicConfigurer.deleteConfig(CONFIG_NAME, name);
    logger.info("IDataAccessObject for {} is un-registered", candidate.getClass().getName());
  }

  
  @Activate
  private void activate() {
    logger.info(".... activate");
    activated = true;
    
    // Register all deferred IEntity
    while (deferred.size() > 0) {
      IEntity entity = deferred.remove(0);
      logger.info("... registering " + entity);
      register(entity);
    }
  }
  
  
  @Deactivate
  private void deactivate() {
    logger.info(".... de-activate..........................................................");
    deferred.clear();
    activated = false;  
  }
  
}
