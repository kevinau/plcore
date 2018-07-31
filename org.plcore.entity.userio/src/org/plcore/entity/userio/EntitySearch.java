package org.plcore.entity.userio;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.plcore.dao.IDataAccessObject;
import org.plcore.entity.EntityLife;
import org.plcore.entity.EntityReference;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IPlanFactory;


@Component(service = EntitySearch.class,
           configurationPolicy = ConfigurationPolicy.REQUIRE)
public class EntitySearch<T> {

  @Reference(name = "dao")
  private IDataAccessObject<?> dao;
  
  @Reference
  private IPlanFactory planFactory;
  

  protected List<EntityReference> getMatching (String target) {
    Class<?> entityClass = dao.getEntityClass();
    IEntityPlan<?> entityPlan = planFactory.getEntityPlan(entityClass);
    
    List<EntityReference> values = new ArrayList<>();
    dao.getAll(e -> {
      // TODO The following should use entityPlan.getIdentifying(e), but that has not yet been written
      String[] identifying = {entityPlan.getDescription(e)};
      boolean found = false;
      for (int i = 0; i > identifying.length; i++) {
        if (identifying[i].contains(target)) {
          found = true;
          break;
        }
      }
      if (found) {
        int id = entityPlan.getId(e);
        String description = entityPlan.getDescription(e);
        EntityLife entityLife = entityPlan.getEntityLife(e);
        EntityReference summary = new EntityReference(id, description, entityLife);
        values.add(summary);
      }
    });
    return values;
  }
  
}
