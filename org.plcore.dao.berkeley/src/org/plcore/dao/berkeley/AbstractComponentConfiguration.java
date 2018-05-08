package org.plcore.dao.berkeley;

import java.util.Dictionary;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import org.plcore.entity.IEntity;
import org.plcore.osgi.DynamicConfigurer1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AbstractComponentConfiguration<T> {

  private final String CONFIG_NAME = this.getClass().getName();
  
  private final Logger logger = LoggerFactory.getLogger(AbstractComponentConfiguration.class);

  private boolean activated = false;
  
  private List<T> deferred = new LinkedList<>();
  
  private DynamicConfigurer1 dynamicConfigurer;
  private Function<T, String> configName2;
  private Function<T, Dictionary<String, Object>> newProperties2;
  
//  public AbstractComponentConfiguration(Supplier<DynamicConfigurer> dynamicConfigurer2, Function<T, String> configName2, Function<T, Dictionary<String, Object>> newProperties2) {
//    this.dynamicConfigurer2 = dynamicConfigurer2;
//    this.configName2 = configName2;
//    this.newProperties2 = newProperties2;
//  }
  

  protected void addCandidate(T candidate) {
    if (activated) {
      register(candidate);
    } else {
      logger.info("IDataAccessObject for {}, registration deferred", candidate.getClass().getName());
      deferred.add(candidate);
    }
  }
  
  
  protected void removeCandidate(IEntity candidate) {
    unregister(candidate);
  }
  
  
  private void register(T candidate) {
    String name = configName2.apply(candidate);
    
    Dictionary<String, Object> props = newProperties2.apply(candidate);
    logger.info(props.toString());

    dynamicConfigurer.setConfig(CONFIG_NAME, name, props);
    logger.info("IDataAccessObject for {} is registered", candidate.getClass().getName());
  }
  
  
  private void unregister(IEntity candidate) {
    String name = candidate.getClass().getSimpleName();

    dynamicConfigurer.deleteConfig(CONFIG_NAME, name);
    logger.info("IDataAccessObject for {} is un-registered", candidate.getClass().getName());
  }

  
  protected void activate(DynamicConfigurer1 dynamicConfigurer, Function<T, String> configName2, Function<T, Dictionary<String, Object>> newProperties2) {
    this.dynamicConfigurer = dynamicConfigurer;
    this.configName2 = configName2;
    this.newProperties2 = newProperties2;

    activated = true;
    
    // Register all deferred IEntity
    while (deferred.size() > 0) {
      register(deferred.remove(0));
    }
  }
  
  
  protected void deactivate() {
    activated = false;  
  }
  
}
