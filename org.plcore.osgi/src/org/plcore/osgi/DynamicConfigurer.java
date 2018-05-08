package org.plcore.osgi;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Constants;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.plcore.util.DictionaryAsMap;
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
@Component (service = DynamicConfigurer.class, immediate = true)
public class DynamicConfigurer {

  private final static String REFERENCE = "org.plcore.osgi.DynamicConfigurer";

  private final Logger logger = LoggerFactory.getLogger(DynamicConfigurer.class);
  
  @Reference
  private ConfigurationAdmin configAdmin;

  @Activate
  private void activate() {
    logger.info(".............ACTIVATE");
  }

  @Deactivate
  private void deactivate() {
    logger.info(".............DE-ACTIVATE");
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
  public boolean setConfig(String pid, String factoryPid, final Dictionary<String, Object> ht) {
    try {
      String reference = pid + (factoryPid == null ? "" : "." + factoryPid);
      System.out.println("...... about to get configuration");
      Configuration config = getConfiguration(reference, pid, factoryPid);
      System.out.println("...... " + config);

      Dictionary<String, Object> props = config.getProperties();
      System.out.println("....... old props " + props);
      Hashtable<String, Object> old = props != null
          ? new Hashtable<String, Object>(new DictionaryAsMap<String, Object>(props))
          : null;
      if (old != null) {
        old.remove(REFERENCE);
        old.remove(Constants.SERVICE_PID);
        old.remove(ConfigurationAdmin.SERVICE_FACTORYPID);
      }

      if (!ht.equals(old)) {
        System.out.println("..... change of confiuration");
        ht.put(REFERENCE, reference);
        if (old == null) {
          logger.info("Creating configuration from " + pid + (factoryPid == null ? "" : "." + factoryPid));
        } else {
          logger.info("Updating configuration from " + pid + (factoryPid == null ? "" : "." + factoryPid));
        }
        System.out.println("..... config update to: " + ht);
        config.update(ht);
        return true;
      } else {
        System.out.println("..... NO hange of configuration");
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
  public void deleteConfig(String pid, String factoryPid) {
    String fileName = pid + (factoryPid == null ? "" : "." + factoryPid);
    logger.info("Deleting configuration from " + pid + " " + factoryPid);
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
