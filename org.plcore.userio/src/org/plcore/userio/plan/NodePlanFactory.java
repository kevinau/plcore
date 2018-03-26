package org.plcore.userio.plan;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.plcore.type.IType;
import org.plcore.userio.CodeSource;
import org.plcore.userio.Embeddable;
import org.plcore.userio.Embedded;
import org.plcore.userio.EntryMode;
import org.plcore.userio.IOField;
import org.plcore.userio.ManyToOne;
import org.plcore.userio.OneToOne;
import org.plcore.userio.plan.impl.ArrayPlan;
import org.plcore.userio.plan.impl.ClassPlan;
import org.plcore.userio.plan.impl.EmbeddedPlan;
import org.plcore.userio.plan.impl.InterfacePlan;
import org.plcore.userio.plan.impl.ItemPlan;
import org.plcore.userio.plan.impl.ListPlan;
import org.plcore.userio.plan.impl.ReferencePlan;


public class NodePlanFactory {

//  public static INodePlan getNodePlan (Class<?> klass) {
//    this (klass, entityName(klass), entityLabel(klass), entityEntryMode(klass), 0);
//  }
  
  public static INodePlan getNodePlan (PlanFactory planFactory, Type fieldType, MemberValueGetterSetter field, String name, EntryMode entryMode, int dimension, boolean optional) {
    INodePlan nodePlan;
    
    if (fieldType instanceof GenericArrayType) {
      Type type1 = ((GenericArrayType)fieldType).getGenericComponentType();
      nodePlan = new ArrayPlan(planFactory, field, (Class<?>)type1, name, entryMode, dimension);
    } else if (fieldType instanceof ParameterizedType) {
      ParameterizedType ptype = (ParameterizedType)fieldType;
      Type type1 = ptype.getRawType();
      if (type1.equals(List.class)) {
        Type[] typeArgs = ptype.getActualTypeArguments();
        if (typeArgs.length != 1) {
          throw new IllegalArgumentException("List must have one, and only one, type parameter");
        }
        Type type2 = typeArgs[0];
        nodePlan = new ListPlan(planFactory, field, (Class<?>)type2, name, entryMode, dimension);
      } else {
        throw new IllegalArgumentException("Parameterized type that is not a List");
      }
    } else if (fieldType instanceof Class) {
      Class<?> klass = (Class<?>)fieldType;
      if (klass.isArray()) {
        Type type1 = klass.getComponentType();
        nodePlan = new ArrayPlan(planFactory, field, (Class<?>)type1, name, entryMode, dimension);
      } else {
        nodePlan = getNodePlanPart2(planFactory, field, fieldType, name, entryMode, dimension);
      }
    } else {
      throw new IllegalArgumentException("Unsupported type: " + fieldType);
    }
    return nodePlan;

//    
//    
//    if (ItemTypeRegistry.isItemType(klass)) {
//      return new ItemPlan(klass, field, name, label, entryMode);
//    } else if (klass.isArray()) {
//      return new ArrayPlan(klass, field, name, label, entryMode, dimension);
//    } else {
//      
//    }
//    if (klass.isEnum()) {
//      
//    }
//    IClassPlan<T> plan = (IClassPlan<T>)planCache.get(klass);
//    if (plan == null) {
//      plan = new ClassPlan<T>(klass);
//      planCache.put(klass, plan);
//    }
//    return plan;
  }
  
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static INodePlan getNodePlanPart2 (PlanFactory planFactory, MemberValueGetterSetter field, Type fieldType, String name, EntryMode entryMode, int dimension) {
    INodePlan nodePlan;
  
    // Is there a type declaration within the class
    Class<?> fieldClass = (Class<?>)fieldType;
    IOField itemFieldAnn = field.getAnnotation(IOField.class);
    CodeSource codeSourceAnn = field.getAnnotation(CodeSource.class);
    if (codeSourceAnn != null) {
      
    }
  
    // Is there a named IType for the field (via type parameter of the ItemField annotation),
    // or does the field type match one of the build in field types
    IType<?> type = planFactory.lookupAndResolveType(fieldClass, name, itemFieldAnn);
    if (type != null) {
      nodePlan = new ItemPlan(field, name, entryMode, type);
    } else {
      // Is it a reference type (identified by the ManyToOne annotation).
      ManyToOne fkAnn = field.getAnnotation(ManyToOne.class);
      if (fkAnn != null) {
        nodePlan = new ReferencePlan(planFactory, field, fieldClass, field.getName(), entryMode);
      } else {
        // A reference type can also be identified by the OneToOne annotation.
        OneToOne fkAnn1 = field.getAnnotation(OneToOne.class);
        if (fkAnn1 != null) {
          nodePlan = new ReferencePlan(planFactory, field, fieldClass, field.getName(), entryMode);
        } else {
          // Is it a class type (identified by the Embedded annotation).  The class is traversed and all
          // members are considered as potential entry fields.
          boolean embdAnn = field.isAnnotationPresent(Embedded.class);
          if (embdAnn) {
            ClassPlan<?> classPlan = planFactory.getClassPlan(fieldClass);
            nodePlan = new EmbeddedPlan(field, classPlan, field.getName(), entryMode);
            //nodePlan = new EmbeddedPlan(planFactory, field, fieldClass, field.getName(), entryMode);
          } else {
            // The Embeddable annotation on the field class also identifies a class type.
            boolean emblAnn = fieldClass.isAnnotationPresent(Embeddable.class);
            if (emblAnn) {
              ClassPlan<?> classPlan = planFactory.getClassPlan(fieldClass);
              nodePlan = new EmbeddedPlan(field, classPlan, field.getName(), entryMode);
            } else if (fieldClass.isInterface()) {
              nodePlan = new InterfacePlan(planFactory, field, fieldType, name, entryMode);
            } else {
              //If within a collection (array or list) any object that is not a item, is an embedded class type.
              if (dimension >= 0) {
                ClassPlan<?> classPlan = planFactory.getClassPlan(fieldClass);
                nodePlan = new EmbeddedPlan(field, classPlan, field.getName(), entryMode);
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
   
}
