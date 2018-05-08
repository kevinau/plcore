package org.plcore.dao.berkeley;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.plcore.entity.IEntity;
import org.plcore.osgi.DynamicConfigurer;

@Component(service = DataAccessObjectConfiguration.class, immediate = true)
public class DataAccessObjectConfiguration {

  @Reference
  private ConfigurationAdmin configAdmin;
  
  private DynamicConfigurer<IEntity> componentConfigurer = new DynamicConfigurer<>(DataAccessObject.class);
  
  
  @Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MULTIPLE)
  private void addCandidate(IEntity candidate) {
    componentConfigurer.addCandidate(candidate);
  }
  
  
  @SuppressWarnings("unused")
  private void removeCandidate(IEntity candidate) {
    componentConfigurer.removeCandidate(candidate);
  }
  
  
  @Activate
  private void activate() {
    componentConfigurer.activate(configAdmin, (candidate, props) -> {
      String name = candidate.getClass().getSimpleName();
      props.put("name", name);
      
      String className = candidate.getClass().getName();
      if (className.startsWith("org.plcore.")) {
        props.put("store.target", "(name=CoreDataStore)");
      }
      props.put("class", className);
    });
  }
  
  
  @Deactivate
  private void deactivate() {
    componentConfigurer.deactivate();
  }
  
}
