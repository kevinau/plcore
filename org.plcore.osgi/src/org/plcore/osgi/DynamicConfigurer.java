package org.plcore.osgi;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.plcore.util.DictionaryAsMap;
import org.plcore.util.TriConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A component that allows OSGi components to be configured dynamically.
 * 
 * This allows components to programmatically configure target components,
 * thus "satisfying" the target components.  This is logically equivalent
 * to writing a config file to a directory watched by Felix "fileinstall",
 * with fileinstall observing the new config file and configuring (and hence 
 * satisfying) the target components.
 * 
 * The code here does not write any config file, but the code was derived
 * from Felix fileinstall.
 * 
 */
public class DynamicConfigurer<T> {

  private final static String REFERENCE = "org.plcore.osgi.DynamicConfigurer";

  private final Logger logger = LoggerFactory.getLogger(DynamicConfigurer.class);
  
  private boolean activated = false;
  
  private final List<T> deferred = new LinkedList<>();
  
  private final Class<?> targetComponent;

  private ConfigurationAdmin configAdmin;
  private Function<T, String> candidateNameFn;
  private TriConsumer<T, String, Dictionary<String, Object>> propsFn;
  
  
  public DynamicConfigurer (Class<?> targetComponent) {
    this.targetComponent = targetComponent;
  }
  
  
  public void addCandidate(T candidate) {
    if (activated) {
      register(candidate);
    } else {
      logger.info("Deferred {} registration for {}", targetComponent.getSimpleName(), candidate.getClass().getName());
      deferred.add(candidate);
    }
  }
  
  
  public void removeCandidate(T candidate) {
    unregister(candidate);
  }
  
  
  private void register(T candidate) {
    String name = candidateNameFn.apply(candidate);
    Dictionary<String, Object> props = new Hashtable<>();
    propsFn.accept(candidate, name, props);
    setConfig(targetComponent.getName(), name, props);
    logger.info("{} is registered for {}", targetComponent.getSimpleName(), candidate.getClass().getName());
  }
  
  
  private void unregister(T candidate) {
    String name = candidateNameFn.apply(candidate);
    deleteConfig(targetComponent.getName(), name);
    logger.info("{} is un-registered for {}", targetComponent.getSimpleName(), candidate.getClass().getName());
  }


  public void activate(ConfigurationAdmin configAdmin, 
        TriConsumer<T, String, Dictionary<String, Object>> propsFn) {
    activate(configAdmin, c -> c.getClass().getSimpleName(), propsFn);
  }
    
    
  public void activate(ConfigurationAdmin configAdmin, 
        Function<T, String> candidateNameFn, TriConsumer<T, String, Dictionary<String, Object>> propsFn) {
    this.configAdmin = configAdmin;
    this.candidateNameFn = candidateNameFn;
    this.propsFn = propsFn;
    
    activated = true;
    
    // Register all deferred IEntity
    while (deferred.size() > 0) {
      T entity = deferred.remove(0);
      register(entity);
    }
  }
  
  
  public void deactivate() {
    deferred.clear();
    activated = false;  
  }


  /**
   * Set the configuration based on a Dictionary of properties.
   *
   * @param pid
   *            PID of the component to be configured
   * @param factoryPid
   *            Factory PID of the component to be configured
   * @param ht
   *            a Dictionary of properties
   * @return <code>true</code> if the configuration has been updated
   * @throws Exception
   */
  private boolean setConfig(String pid, String factoryPid, final Dictionary<String, Object> ht) {
    try {
      String reference = pid + (factoryPid == null ? "" : "." + factoryPid);
      Configuration config = getConfiguration(reference, pid, factoryPid);
      Dictionary<String, Object> props = config.getProperties();
      Hashtable<String, Object> old = props != null
          ? new Hashtable<String, Object>(new DictionaryAsMap<String, Object>(props))
          : null;
      if (old != null) {
        old.remove(REFERENCE);
        old.remove(Constants.SERVICE_PID);
        old.remove(ConfigurationAdmin.SERVICE_FACTORYPID);
      }

      if (!ht.equals(old)) {
        ht.put(REFERENCE, reference);
//        if (old == null) {
//          logger.info("Creating configuration from " + pid + (factoryPid == null ? "" : "." + factoryPid));
//        } else {
//          logger.info("Updating configuration from " + pid + (factoryPid == null ? "" : "." + factoryPid));
//        }
        config.update(ht);
        return true;
      } else {
        return false;
      }
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }


  /**
   * Remove the configuration.
   *
   * @param pid
   *            PID of the component to be configured
   * @param factoryPid
   *            Factory PID of the component to be configured
   * @throws Exception
   */
  private void deleteConfig(String pid, String factoryPid) {
    String fileName = pid + (factoryPid == null ? "" : "." + factoryPid);
//    logger.info("Deleting configuration from " + pid + " " + factoryPid);
    try {
      Configuration config = getConfiguration(fileName, pid, factoryPid);
      config.delete();
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }


  private Configuration getConfiguration(String reference, String pid, String factoryPid) throws Exception {
    Configuration oldConfiguration = findExistingConfiguration(reference);
    if (oldConfiguration != null) {
      return oldConfiguration;
    } else {
      Configuration newConfiguration;
      if (factoryPid != null) {
        newConfiguration = configAdmin.createFactoryConfiguration(pid, null);
      } else {
        newConfiguration = configAdmin.getConfiguration(pid, null);
      }
      return newConfiguration;
    }
  }


  private Configuration findExistingConfiguration(String reference) throws Exception {
    String filter = "(" + REFERENCE + "=" + reference + ")";
    Configuration[] configurations = configAdmin.listConfigurations(filter);
    if (configurations != null && configurations.length > 0) {
      return configurations[0];
    } else {
      return null;
    }
  }

}
