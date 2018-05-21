package org.plcore.entity.userio;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.plcore.entity.IEntity;
import org.plcore.osgi.DynamicConfigurer;

@Component(service = EntityCodeTypeConfiguration.class, immediate = true)
public class EntityCodeTypeConfiguration {

  @Reference
  private ConfigurationAdmin configAdmin;
  
  private DynamicConfigurer<IEntity> componentConfigurer = new DynamicConfigurer<>(EntityCodeType.class);
  
  
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
    componentConfigurer.activate(configAdmin, candidate -> candidate.getClass().getSimpleName() + "Type", (candidate, name, props) -> {
      props.put("name", name);
      
      // The daoName does not end with "Type".  The name passed (above) is suffixed with "Type".
      String daoName = candidate.getClass().getSimpleName();
      props.put("dao.target", "(name=" + daoName + ")");
    });
  }
  
  
  @Deactivate
  private void deactivate() {
    componentConfigurer.deactivate();
  }
  
}
