package org.plcore.userio.plan.impl;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.type.IType;
import org.plcore.type.TypeRegistry;
import org.plcore.userio.CodeSource;
import org.plcore.userio.Embeddable;
import org.plcore.userio.Embedded;
import org.plcore.userio.EntryMode;
import org.plcore.userio.IOField;
import org.plcore.userio.ManyToOne;
import org.plcore.userio.OneToOne;
import org.plcore.userio.plan.IAugmentedClass;
import org.plcore.userio.plan.IEmbeddedPlan;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.IPlanFactory;
import org.plcore.userio.plan.MemberValueGetterSetter;
import org.plcore.userio.plan.TypeResolver;


@Component
public class PlanFactory implements IPlanFactory {

  @Reference
  private TypeRegistry typeRegistry;
  
  private Map<Class<?>, IAugmentedClass<?>> classPlans = new HashMap<>();

  
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
    IAugmentedClass<T> aclass = getAugmentedClass(klass);
    EntityPlan<T> plan = new EntityPlan<T>(aclass);
    plan.complete(this);
    return plan;
  }

  
  @Override
  public <T> IEmbeddedPlan<T> getEmbeddedPlan(Class<T> klass) {
    IAugmentedClass<T> aclass = getAugmentedClass(klass);
    IEmbeddedPlan<T> plan = new EmbeddedPlan<>(aclass);
    return plan;
  }

  
  @Override
  public INodePlan getNodePlan (Type fieldType, MemberValueGetterSetter field, String name, EntryMode entryMode, int dimension, boolean optional) {
    INodePlan nodePlan;
    
    if (fieldType instanceof GenericArrayType) {
      Type type1 = ((GenericArrayType)fieldType).getGenericComponentType();
      nodePlan = new ArrayPlan(this, field, (Class<?>)type1, name, entryMode, dimension);
    } else if (fieldType instanceof ParameterizedType) {
      ParameterizedType ptype = (ParameterizedType)fieldType;
      Type type1 = ptype.getRawType();
      if (type1.equals(List.class)) {
        Type[] typeArgs = ptype.getActualTypeArguments();
        if (typeArgs.length != 1) {
          throw new IllegalArgumentException("List must have one, and only one, type parameter");
        }
        Type type2 = typeArgs[0];
        nodePlan = new ListPlan(this, field, (Class<?>)type2, name, entryMode, dimension);
      } else {
        throw new IllegalArgumentException("Parameterized type that is not a List");
      }
    } else if (fieldType instanceof Class) {
      Class<?> klass = (Class<?>)fieldType;
      if (klass.isArray()) {
        Type type1 = klass.getComponentType();
        nodePlan = new ArrayPlan(this, field, (Class<?>)type1, name, entryMode, dimension);
      } else {
        nodePlan = getNodePlanPart2(field, fieldType, name, entryMode, dimension);
      }
    } else {
      throw new IllegalArgumentException("Unsupported type: " + fieldType);
    }
    return nodePlan;
  }
  
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private INodePlan getNodePlanPart2 (MemberValueGetterSetter field, Type fieldType, String name, EntryMode entryMode, int dimension) {
    INodePlan nodePlan;
  
    // Is there a type declaration within the class
    Class<?> fieldClass = (Class<?>)fieldType;
    IOField itemFieldAnn = field.getAnnotation(IOField.class);
    CodeSource codeSourceAnn = field.getAnnotation(CodeSource.class);
    if (codeSourceAnn != null) {
      
    }
  
    // Is there a named IType for the field (via type parameter of the ItemField annotation),
    // or does the field type match one of the build in field types
    IType<?> type = lookupAndResolveType(fieldClass, name, itemFieldAnn);
    if (type != null) {
      nodePlan = new ItemPlan(field, name, entryMode, type);
    } else {
      // Is it a reference type (identified by the ManyToOne annotation).
      ManyToOne fkAnn = field.getAnnotation(ManyToOne.class);
      if (fkAnn != null) {
        nodePlan = new ReferencePlan(this, field, fieldClass, field.getName(), entryMode);
      } else {
        // A reference type can also be identified by the OneToOne annotation.
        OneToOne fkAnn1 = field.getAnnotation(OneToOne.class);
        if (fkAnn1 != null) {
          nodePlan = new ReferencePlan(this, field, fieldClass, field.getName(), entryMode);
        } else {
          // Is it a class type (identified by the Embedded annotation).  The class is traversed and all
          // members are considered as potential entry fields.
          boolean embdAnn = field.isAnnotationPresent(Embedded.class);
          if (embdAnn) {
            IAugmentedClass<?> aclass = getAugmentedClass(fieldClass);
            nodePlan = new EmbeddedPlan(field, aclass, field.getName(), entryMode);
            //nodePlan = new EmbeddedPlan(planFactory, field, fieldClass, field.getName(), entryMode);
          } else {
            // The Embeddable annotation on the field class also identifies a class type.
            boolean emblAnn = fieldClass.isAnnotationPresent(Embeddable.class);
            if (emblAnn) {
              IAugmentedClass<?> aclass = getAugmentedClass(fieldClass);
              nodePlan = new EmbeddedPlan(field, aclass, field.getName(), entryMode);
            } else if (fieldClass.isInterface()) {
              nodePlan = new InterfacePlan(this, field, fieldType, name, entryMode);
            } else {
              //If within a collection (array or list) any object that is not a item, is an embedded class type.
              if (dimension >= 0) {
                IAugmentedClass<?> aclass = getAugmentedClass(fieldClass);
                nodePlan = new EmbeddedPlan(field, aclass, field.getName(), entryMode);
              } else {
                // Otherwise, throw an error.
                throw new RuntimeException("Field type not recognised: " + name + " " + fieldType);
              }
            }
          }
        }
      }
    }
    return nodePlan;
  }
   

  @SuppressWarnings("unchecked")
  @Override
  public <T> IAugmentedClass<T> getAugmentedClass(Class<T> klass) {
    IAugmentedClass<T> aclass;
    synchronized (classPlans) {
      aclass = (AugmentedClass<T>)classPlans.get(klass);
      if (aclass == null) {
        aclass = new AugmentedClass<T>(klass);
        classPlans.put(klass, aclass);
        aclass.complete(this);
      }
    }
    return aclass;
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
    
    type = typeRegistry.getByFieldClass(fieldClass, fieldName);
    if (type == null) {
      return null;
    }
    
    return TypeResolver.resolve(type, fieldAnn);
  }

}
