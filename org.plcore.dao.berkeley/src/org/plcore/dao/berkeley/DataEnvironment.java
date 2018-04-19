package org.plcore.dao.berkeley;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.plcore.home.IApplication;
import org.plcore.osgi.Configurable;
import org.plcore.osgi.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseNotFoundException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.StoreConfig;


@Component(service = DataEnvironment.class, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class DataEnvironment {

  private Logger logger = LoggerFactory.getLogger(DataEnvironment.class);
  
  @Reference
  private IApplication application;
  
  @Reference
  private ConfigurationLoader configLoader;
  
  @Configurable
  /**
   * Is this environment for plcore data or application data.  True if
   * this environment is for plcore data and false otherwise.
   */
  private boolean isCoreData = false;
  
  private Environment envmnt;

  
  @Activate
  public void activate (ComponentContext componentContext) {
    configLoader.load(this, componentContext);
    
    String envDir = (isCoreData ? "coreData" : "applicationData");
    Path envHome = application.getBaseDir().resolve(envDir);
    
    // Create directory if it does not exist
    try {
      Files.createDirectories(envHome);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    logger.info("activate: envHome {}", envHome);
    
    // Set up the environment
    EnvironmentConfig envConfig = new EnvironmentConfig();
    envConfig.setAllowCreate(true);
    envConfig.setTransactional(true);

    // Open the environment
    envmnt = new Environment(envHome.toFile(), envConfig);
  }
  
  
  @Deactivate
  public void deactivate () {
    logger.info("deactivate");
    envmnt.close();
  }
  
  
  public EntityStore newEntityStore (String storeName, StoreConfig storeConfig) {
    return new EntityStore(envmnt, storeName, storeConfig);
  }
  
  
  public Database openDatabase (Transaction trans, String name, DatabaseConfig dbConfig) {
    return envmnt.openDatabase(trans, name, dbConfig);
  }
  
  
  public SecondaryDatabase openSecondaryDatabase (Transaction trans, String name, Database primaryDatabase, SecondaryConfig secondaryConfig) {
    return envmnt.openSecondaryDatabase(trans, name, primaryDatabase, secondaryConfig);
  }
  
  
  public Transaction beginTransaction () {
    return envmnt.beginTransaction(null, null);
  }

  
  public Transaction beginTransaction (Transaction trans, TransactionConfig transConfig) {
    return envmnt.beginTransaction(trans, transConfig);
  }
  
  
  public void truncateDatabase (String name) {
    String prefix = name + "_";
    
    Transaction txn = beginTransaction();
    List<String> names = envmnt.getDatabaseNames();
    for (String n : names) {
      if (n.equals(name) || n.startsWith(prefix)) {
        try {
          envmnt.truncateDatabase(txn, n, false);
          logger.info("Database truncated: {}", n);
        } catch (DatabaseNotFoundException ex) {
          // Do nothing if the database cannot be found
        } catch (Exception ex) {
          // But catch any other errors
          txn.abort();
          throw ex;
        }
      }
    }
    txn.commit();
  }
  
}

