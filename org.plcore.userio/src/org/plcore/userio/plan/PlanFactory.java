package org.plcore.userio.plan;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.type.IType;
import org.plcore.type.TypeRegistry;
import org.plcore.userio.IOField;
import org.plcore.userio.plan.impl.ClassPlan;
import org.plcore.userio.plan.impl.EntityPlan;


@Component
public class PlanFactory implements IPlanFactory {

  @Reference
  private TypeRegistry typeRegistry;
  
  private Map<Class<?>, ClassPlan<?>> classPlans = new HashMap<>();

  
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
  public <T> IEntityPlan<T> getEntityPlan(Class<T> klass) {
    ClassPlan<T> classPlan = getClassPlan(klass);
    EntityPlan<T> plan = new EntityPlan<T>(classPlan);
    //plan.complete(this);
    return plan;
  }

  
  @SuppressWarnings("unchecked")
  public <T> ClassPlan<T> getClassPlan(Class<T> klass) {
    ClassPlan<T> plan;
    synchronized (classPlans) {
      plan = (ClassPlan<T>)classPlans.get(klass);
      if (plan == null) {
        plan = new ClassPlan<T>(klass);
        classPlans.put(klass, plan);
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
