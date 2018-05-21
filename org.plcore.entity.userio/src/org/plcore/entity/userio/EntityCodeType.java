package org.plcore.entity.userio;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.plcore.dao.IDataAccessObject;
import org.plcore.entity.EntityLife;
import org.plcore.entity.EntityReference;
import org.plcore.type.IType;
import org.plcore.type.builtin.CodeType;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IPlanFactory;


@Component(service = {IType.class, EntityCodeType.class},
           configurationPolicy = ConfigurationPolicy.REQUIRE)
public class EntityCodeType extends CodeType<EntityReference> {

  @Reference(name = "dao")
  private IDataAccessObject<?> dao;
  
  @Reference
  private IPlanFactory planFactory;
  

  @Override
  protected List<EntityReference> getValues () {
    Class<?> entityClass = dao.getEntityClass();
    IEntityPlan<?> entityPlan = planFactory.getEntityPlan(entityClass);
    
    List<EntityReference> values = new ArrayList<>();
    dao.getAll(e -> {
      int id = entityPlan.getId(e);
      String description = entityPlan.getDescription(e);
      EntityLife entityLife = entityPlan.getEntityLife(e);
      EntityReference summary = new EntityReference(id, description, entityLife);
      values.add(summary);
    });
    return values;
  }
  

  @Override
  public Class<?> getFieldClass() {
    return EntityCodeType.class;
  }

  
}
