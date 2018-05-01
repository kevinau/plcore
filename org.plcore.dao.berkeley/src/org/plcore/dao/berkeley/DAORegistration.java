package org.plcore.dao.berkeley;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.plcore.dao.IDAOCandidate;
import org.plcore.dao.IDataAccessObject;
import org.plcore.osgi.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = DAORegistration.class, immediate = true)
public class DAORegistration {

  private final Logger logger = LoggerFactory.getLogger(DAORegistration.class);
  
  @Reference(name = "store")
  private DataStore dataStore;
  
  @Reference
  private ConfigurationLoader configLoader;
  
  private BundleContext bundleContext = null;
  
  private List<IDAOCandidate> deferred = new LinkedList<>();
  
  private Map<IDAOCandidate, ServiceRegistration<?>> registrations = new HashMap<>();
  
  
  @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
  private void addCandidate(IDAOCandidate candidate) {
    if (bundleContext == null) {
      logger.info("IDataAccessObject for {}, registration deferred", candidate.getClass().getName());
      deferred.add(candidate);
    } else {
      register(candidate);
    }
  }
  
  
  @SuppressWarnings("unused")
  private void removeCandidate(IDAOCandidate candidate) {
    unregister(candidate);
  }
  
  
  private void register(IDAOCandidate candidate) {
    Map<String, Object> props = new HashMap<>();
    props.put("name", candidate.getClass().getSimpleName());
    // TODO The following 2 lines are Berkeley db specific!!!!
    props.put("store.target", "(name=DefaultDataStore)");
    props.put("class", candidate.getClass().getName());
    logger.info(props.toString());
    
    DataAccessObject<?> dao = new DataAccessObject<>(dataStore, configLoader);
    dao.activate(props);
    
    ServiceRegistration<?> registration = bundleContext.registerService(IDataAccessObject.class.getName(), dao, new Hashtable<String, Object>(props));
    registrations.put(candidate, registration);
    logger.info("IDataAccessObject for {} is registered", candidate.getClass().getName());
  }
  
  
  private void unregister(IDAOCandidate candidate) {
    ServiceRegistration<?> registration = registrations.get(candidate);
    if (registration != null) {
      registration.unregister();
      logger.info("IDataAccessObject for {} is un-registered", candidate.getClass().getName());
    }
  }

  
  @Activate
  private void activate(BundleContext bundleContext) {
    this.bundleContext = bundleContext;
    
    // Register all deferred dao possibles
    while (deferred.size() > 0) {
      register(deferred.remove(0));
    }
  }
  
  
  @Deactivate
  private void deactivate() {
    // Unregister any remaining registrations
    for (ServiceRegistration<?> registration : registrations.values()) {
      registration.unregister();
    }
    registrations.clear();
    deferred.clear();
    bundleContext = null;  
  }
  
}
