package org.plcore.userio.plan;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.type.IType;
import org.plcore.type.TypeRegistry;
import org.plcore.userio.EntryMode;
import org.plcore.userio.IOField;
import org.plcore.userio.plan.impl.EmbeddedPlan;
import org.plcore.userio.plan.impl.EntityPlan;


@Component
public class PlanFactory implements IPlanFactory {

  @Reference
  private TypeRegistry typeRegistry;
  
  private Map<Class<?>, EntityPlan<?>> entityPlans = new HashMap<>();
  private Map<Class<?>, EmbeddedPlan<?>> embeddedPlans = new HashMap<>();

  
  public PlanFactory () {
  }
  
  
  public PlanFactory (TypeRegistry typeRegistry) {
    this.typeRegistry = typeRegistry;
  }

  
  @Override
  @SuppressWarnings("unchecked")
  public <T> IEntityPlan<T> getEntityPlan(String klassName) {
    Class<T> klass;
    try {
      klass = (Class<T>)Class.forName(klassName);
    } catch (ClassNotFoundException ex) {
      throw new RuntimeException(ex);
    }
    return getEntityPlan(klass);
  }


  @Override
  @SuppressWarnings("unchecked")
  public <T> IEntityPlan<T> getEntityPlan(Class<T> klass) {
    EntityPlan<T> plan;
    synchronized (entityPlans) {
      plan = (EntityPlan<T>)entityPlans.get(klass);
      if (plan == null) {
        plan = new EntityPlan<T>(klass);
        entityPlans.put(klass, plan);
        plan.complete(this);
      }
    }
    return plan;
  }

  
  @SuppressWarnings("unchecked")
  public <T> IEmbeddedPlan<T> getEmbeddedPlan(MemberValueGetterSetter field, Class<T> klass, String name, EntryMode entryMode) {
    EmbeddedPlan<T> plan;
    synchronized (embeddedPlans) {
      plan = (EmbeddedPlan<T>)embeddedPlans.get(klass);
      if (plan == null) {
        plan = new EmbeddedPlan<T>(field, klass, name, entryMode);
        embeddedPlans.put(klass, plan);
        plan.complete(this);
      }
    }
    return plan;
  }

  
  public IType<?> lookupAndResolveType (Class<?> fieldClass, String fieldName, IOField fieldAnn) {
    IType<?> type;
    
    if (fieldAnn != null) {
      String namedType = fieldAnn.type();
      if (namedType.length() > 0) {
        type = typeRegistry.getByName(namedType);
        if (type == null) {
          throw new RuntimeException("Field " + fieldName + " expects a named type '" + namedType + "'. No type found");
        }
        return type;
      }
    }
    
    type = typeRegistry.getByFieldClass(fieldClass);
    if (type == null) {
      return null;
    }
    
    return TypeResolver.resolve(type, fieldAnn);
  }

}
