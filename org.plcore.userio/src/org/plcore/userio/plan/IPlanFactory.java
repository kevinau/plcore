package org.plcore.userio.plan;

import java.lang.reflect.Type;

import org.plcore.userio.EntryMode;

public interface IPlanFactory {

  public <T> IEntityPlan<T> getEntityPlan(Class<T> klass);

  public <T> IEntityPlan<T> getEntityPlan(String klassName);

  public <T> IEmbeddedPlan<T> getEmbeddedPlan(Class<T> klass);

  public <T> IAugmentedClass<T> getAugmentedClass(Class<T> klass);

  public INodePlan getNodePlan(Type fieldType, MemberValueGetterSetter field, String name, EntryMode entryMode, int dimension, boolean optional);

}
