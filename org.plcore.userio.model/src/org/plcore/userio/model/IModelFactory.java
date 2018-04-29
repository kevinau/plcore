package org.plcore.userio.model;

import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IPlanFactory;

public interface IModelFactory {

  public IEntityModel buildEntityModel(Class<?> entityClass);

  public IEntityModel buildEntityModel(IEntityPlan<?> entityPlan);

  public IEntityModel buildEntityModel(String entityClassName) throws ClassNotFoundException;

  public IEmbeddedModel buildEmbeddedModel(Class<?> embeddedClass);
  
  public IPlanFactory getPlanFactory();

}
