/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 * Changes by KH:
 *  - Removed the setting of configuration from files (effectively removing ArtifactInstaller
 *  - Added programmatic setting of configuration
 *  - Reformatted code to conform to our standard
 *  - Updated comments
 */
package org.apache.felix.fileinstall.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.plcore.util.DictionaryAsMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * ArtifactInstaller for configurations.
 * TODO: This service lifecycle should be bound to the ConfigurationAdmin service lifecycle.
 */
public class ConfigInstaller {

  public final static String REFERENCE = "org.plcore.osgi.dynconfig.Reference";

  private final Logger logger = LoggerFactory.getLogger(ConfigInstaller.class);
  
  private final BundleContext context;
  private final ConfigurationAdmin configAdmin;
  private ServiceRegistration<?> registration;


  ConfigInstaller(BundleContext context, ConfigurationAdmin configAdmin) {
    this.context = context;
    this.configAdmin = configAdmin;
  }


  public void init() {
    registration = this.context.registerService(new String[] {
        ConfigInstaller.class.getName(),
    }, this, null);
  }


  public void destroy() {
    registration.unregister();
  }




  ConfigurationAdmin getConfigurationAdmin() {
    return configAdmin;
  }


  /**
   * Set the configuration based on a Dictionary of properties.
   *
   * @param ht
   *            a Dictionary of properties
   * @return <code>true</code> if the configuration has been updated
   * @throws Exception
   */
  boolean setConfig(String pid, String factoryPid, final Dictionary<String, Object> ht) throws Exception {

    String reference = pid + (factoryPid == null ? "" : "-" + factoryPid);
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
        logger.info("Creating configuration from " + pid + (factoryPid == null ? "" : "-" + factoryPid) + ".cfg");
      } else {
        logger.info("Updating configuration from " + pid + (factoryPid == null ? "" : "-" + factoryPid) + ".cfg");
      }
      System.out.println("..... config update to: " + ht);
      config.update(ht);
      return true;
    } else {
      System.out.println("..... NO hange of confiuration");
      return false;
    }
  }


  /**
   * Remove the configuration.
   *
   * @param f
   *            File where the configuration in was defined.
   * @return <code>true</code>
   * @throws Exception
   */
  boolean deleteConfig(String pid, String factoryPid) throws Exception {
    String fileName = pid + (factoryPid == null ? "" : "-" + factoryPid);
    logger.info("Deleting configuration from " + pid + " " + factoryPid);
    Configuration config = getConfiguration(fileName, pid, factoryPid);
    config.delete();
    return true;
  }


  private Configuration getConfiguration(String reference, String pid, String factoryPid) throws Exception {
    Configuration oldConfiguration = findExistingConfiguration(reference);
    if (oldConfiguration != null) {
      return oldConfiguration;
    } else {
      Configuration newConfiguration;
      if (factoryPid != null) {
        newConfiguration = getConfigurationAdmin().createFactoryConfiguration(pid, null);
      } else {
        newConfiguration = getConfigurationAdmin().getConfiguration(pid, null);
      }
      return newConfiguration;
    }
  }


  private Configuration findExistingConfiguration(String reference) throws Exception {
    String filter = "(" + REFERENCE + "=" + reference + ")";
    Configuration[] configurations = getConfigurationAdmin().listConfigurations(filter);
    if (configurations != null && configurations.length > 0) {
      return configurations[0];
    } else {
      return null;
    }
  }

}