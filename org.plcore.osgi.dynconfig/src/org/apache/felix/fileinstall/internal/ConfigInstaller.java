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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.apache.felix.cm.file.ConfigurationHandler;
import org.apache.felix.fileinstall.internal.Util.Logger;
import org.apache.felix.utils.properties.InterpolationHelper;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.plcore.util.DictionaryAsMap;


/**
 * ArtifactInstaller for configurations.
 * TODO: This service lifecycle should be bound to the ConfigurationAdmin service lifecycle.
 */
public class ConfigInstaller {

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


//  @Override
//  public void install(File artifact) throws Exception {
//    setConfig(artifact);
//  }
//
//
//  @Override
//  public void update(File artifact) throws Exception {
//    setConfig(artifact);
//  }
//
//
//  @Override
//  public void uninstall(File artifact) throws Exception {
//    deleteConfig(artifact);
//  }


  ConfigurationAdmin getConfigurationAdmin() {
    return configAdmin;
  }


  /**
   * Set the configuration based on the config file.
   *
   * @param f
   *            Configuration file
   * @return <code>true</code> if the configuration has been updated
   * @throws Exception
   */
  boolean setConfig(final File f) throws Exception {
    final Hashtable<String, Object> ht = new Hashtable<String, Object>();
    final InputStream in = new BufferedInputStream(new FileInputStream(f));
    try {
      if (f.getName().endsWith(".cfg")) {
        final Properties p = new Properties();
        in.mark(1);
        boolean isXml = in.read() == '<';
        in.reset();
        if (isXml) {
          p.loadFromXML(in);
        } else {
          p.load(in);
        }
        Map<String, String> strMap = new HashMap<String, String>();
        for (Object k : p.keySet()) {
          strMap.put(k.toString(), p.getProperty(k.toString()));
        }
        InterpolationHelper.performSubstitution(strMap, context);
        ht.putAll(strMap);
      } else if (f.getName().endsWith(".config")) {
        final Dictionary<String, Object> config = ConfigurationHandler.read(in);
        final Enumeration<String> i = config.keys();
        while (i.hasMoreElements()) {
          final String key = i.nextElement();
          ht.put(key.toString(), config.get(key));
        }
      }
    } finally {
      in.close();
    }

    String pid[] = parsePid(f.getName());
    Configuration config = getConfiguration(toConfigKey(f), pid[0], pid[1]);

    Dictionary<String, Object> props = config.getProperties();
    Hashtable<String, Object> old = props != null
        ? new Hashtable<String, Object>(new DictionaryAsMap<String, Object>(props))
        : null;
    if (old != null) {
      old.remove(DirectoryWatcher.FILENAME);
      old.remove(Constants.SERVICE_PID);
      old.remove(ConfigurationAdmin.SERVICE_FACTORYPID);
    }

    if (!ht.equals(old)) {
      ht.put(DirectoryWatcher.FILENAME, toConfigKey(f));
      if (old == null) {
        Util.log(context, Logger.LOG_INFO,
            "Creating configuration from " + pid[0] + (pid[1] == null ? "" : "-" + pid[1]) + ".cfg", null);
      } else {
        Util.log(context, Logger.LOG_INFO,
            "Updating configuration from " + pid[0] + (pid[1] == null ? "" : "-" + pid[1]) + ".cfg", null);
      }
      config.update(ht);
      return true;
    } else {
      return false;
    }
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

    String fileName = pid + (factoryPid == null ? "" : "-" + factoryPid);
    System.out.println("...... about to get configuration");
    Configuration config = getConfiguration(fileName, pid, factoryPid);
    System.out.println("...... " + config);

    Dictionary<String, Object> props = config.getProperties();
    System.out.println("....... old props " + props);
    Hashtable<String, Object> old = props != null
        ? new Hashtable<String, Object>(new DictionaryAsMap<String, Object>(props))
        : null;
    if (old != null) {
      old.remove(DirectoryWatcher.FILENAME);
      old.remove(Constants.SERVICE_PID);
      old.remove(ConfigurationAdmin.SERVICE_FACTORYPID);
    }

    if (!ht.equals(old)) {
      System.out.println("..... change of confiuration");
      ht.put(DirectoryWatcher.FILENAME, fileName);
      if (old == null) {
        Util.log(context, Logger.LOG_INFO,
            "Creating configuration from " + pid + (factoryPid == null ? "" : "-" + factoryPid) + ".cfg", null);
      } else {
        Util.log(context, Logger.LOG_INFO,
            "Updating configuration from " + pid + (factoryPid == null ? "" : "-" + factoryPid) + ".cfg", null);
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
  boolean deleteConfig(File f) throws Exception {
    String pid[] = parsePid(f.getName());
    Util.log(context, Logger.LOG_INFO,
        "Deleting configuration from " + pid[0] + (pid[1] == null ? "" : "-" + pid[1]) + ".cfg", null);
    Configuration config = getConfiguration(toConfigKey(f), pid[0], pid[1]);
    config.delete();
    return true;
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
    Util.log(context, Logger.LOG_INFO, "Deleting configuration from " + pid + " " + factoryPid, null);
    Configuration config = getConfiguration(fileName, pid, factoryPid);
    config.delete();
    return true;
  }


  String toConfigKey(File f) {
    return f.getAbsoluteFile().toURI().toString();
  }


  File fromConfigKey(String key) {
    return new File(URI.create(key));
  }


  String[] parsePid(String path) {
    String pid = path.substring(0, path.lastIndexOf('.'));
    int n = pid.indexOf('-');
    if (n > 0) {
      String factoryPid = pid.substring(n + 1);
      pid = pid.substring(0, n);
      return new String[] { pid, factoryPid };
    } else {
      return new String[] { pid, null };
    }
  }


  Configuration getConfiguration(String fileName, String pid, String factoryPid) throws Exception {
    Configuration oldConfiguration = findExistingConfiguration(fileName);
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


  Configuration findExistingConfiguration(String fileName) throws Exception {
    String filter = "(" + DirectoryWatcher.FILENAME + "=" + escapeFilterValue(fileName) + ")";
    Configuration[] configurations = getConfigurationAdmin().listConfigurations(filter);
    if (configurations != null && configurations.length > 0) {
      return configurations[0];
    } else {
      return null;
    }
  }


  private String escapeFilterValue(String s) {
    return s.replaceAll("[(]", "\\\\(").replaceAll("[)]", "\\\\)").replaceAll("[=]", "\\\\=").replaceAll("[\\*]",
        "\\\\*");
  }

}