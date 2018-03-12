package org.plcore.userio.plan;


public interface IPlanFactory {

  public <T> IEntityPlan<T> getEntityPlan(Class<T> klass);

  public <T> IEntityPlan<T> getEntityPlan(String klassName);

}
