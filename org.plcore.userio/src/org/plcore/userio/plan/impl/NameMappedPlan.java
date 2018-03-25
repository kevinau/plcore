package org.plcore.userio.plan.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.plcore.type.UserEntryException;
import org.plcore.userio.DefaultFor;
import org.plcore.userio.EntryMode;
import org.plcore.userio.FactoryFor;
import org.plcore.userio.INameMappedNode;
import org.plcore.userio.INode;
import org.plcore.userio.IOField;
import org.plcore.userio.MappedSuperclass;
import org.plcore.userio.Mode;
import org.plcore.userio.ModeFor;
import org.plcore.userio.NotIOField;
import org.plcore.userio.OccursFor;
import org.plcore.userio.Optional;
import org.plcore.userio.Validation;
import org.plcore.userio.ValuesFor;
import org.plcore.userio.plan.IContainerPlan;
import org.plcore.userio.plan.IItemPlan;
import org.plcore.userio.plan.INameMappedPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.IRuntimeDefaultProvider;
import org.plcore.userio.plan.IRuntimeFactoryProvider;
//import org.plcore.userio.plan.IRuntimeImplementationProvider;
import org.plcore.userio.plan.IRuntimeModeProvider;
import org.plcore.userio.plan.IRuntimeOccursProvider;
import org.plcore.userio.plan.IRuntimeValuesProvider;
import org.plcore.userio.plan.IValidationMethod;
import org.plcore.userio.plan.MemberValueGetterSetter;
import org.plcore.userio.plan.NodePlanFactory;
import org.plcore.userio.plan.PlanFactory;
import org.plcore.util.CamelCase;
import org.plcore.value.ICode;


public abstract class NameMappedPlan<T> extends ContainerPlan implements INameMappedPlan, INameMappedNode {

  private final Class<T> klass;

  private final LinkedHashMap<String, INodePlan> memberPlans = new LinkedHashMap<>();
  private final Map<String, MemberValueGetterSetter> memberFields = new LinkedHashMap<>();

  //private List<IRuntimeTypeProvider> runtimeTypeProviders = new ArrayList<>(0);
  //private List<IRuntimeLabelProvider> runtimeLabelProviders = new ArrayList<>(0);
  private List<IRuntimeModeProvider> runtimeModeProviders = new ArrayList<>(0);
//  private List<IRuntimeImplementationProvider> runtimeImplementationProviders = new ArrayList<>(0);
  private List<IRuntimeDefaultProvider> runtimeDefaultProviders = new ArrayList<>(0);
  private List<IRuntimeValuesProvider> runtimeValuesProviders = new ArrayList<>(0);
  private List<IRuntimeFactoryProvider> runtimeFactoryProviders = new ArrayList<>(0);
  private List<IRuntimeOccursProvider> runtimeOccursProviders = new ArrayList<>(0);
  //private List<IRuntimeFactoryProvider2> runtimeFactoryProviders2 = new ArrayList<>(0);
  private Set<IValidationMethod> validationMethods = new TreeSet<IValidationMethod>();


  protected static String entityName (Class<?> entityClass) {
    String klassName = entityClass.getSimpleName();
    return Character.toLowerCase(klassName.charAt(0)) + klassName.substring(1);
  }
  
  
  protected static String entityLabel (Class<?> entityClass) {
    String klassName = entityClass.getSimpleName();
    return CamelCase.toSentence(klassName);
  }
  
  
  protected static EntryMode entityEntryMode (Class<?> entityClass) {
    EntryMode entryMode = EntryMode.UNSPECIFIED;
    Mode modeAnn = entityClass.getAnnotation(Mode.class);
    if (modeAnn != null) {
      entryMode = modeAnn.value();
    }
    return entryMode;
  }
  

  public NameMappedPlan (MemberValueGetterSetter field, Class<T> klass, String name, EntryMode entryMode) {
    super(field, name, entryMode);
    this.klass = klass;
  }
  
  
  public void complete (PlanFactory factory) {
    addClassFields (factory, klass, true);
  }
  
  
  @Override
  public String getClassName() {
    return klass.getCanonicalName();
  }
  
  
//  @SuppressWarnings("unchecked")
//  public static <T> IClassPlan<T> getClassPlan (Class<T> klass) {
//    String className = klass.getSimpleName();
//    IClassPlan<T> plan = (IClassPlan<T>)planCache.get(className);
//    if (plan == null) {
//      plan = new ClassPlan<T>(klass);
//      planCache.put(className, plan);
//    }
//    return plan;
//  }
//   
//  
//  @SuppressWarnings("unchecked")
//  public static <T> IClassPlan<T> getClassPlan (String pathName, String label, Class<T> klass, EntryMode entryMode) {
//    String className = klass.getSimpleName();
//    IClassPlan<T> plan = (IClassPlan<T>)planCache.get(className);
//    if (plan == null) {
//      plan = new ClassPlan<T>(pathName, label, klass, entryMode);
//      planCache.put(className, plan);
//    }
//    return plan;
//  }
   
  
  public void addClassFields (PlanFactory factory, Class<?> klass, boolean include) {
    Field[] declaredFields = klass.getDeclaredFields();
    Method[] declaredMethods = klass.getDeclaredMethods();
    
    addClassFields2 (factory, klass, declaredFields, declaredMethods, include);
  }
  
  
  private static String isIOField(Method method, boolean[] isSetter) {
    if (method.isSynthetic()) {
      // Synthetic methods are not used to identify form members
      throw new IllegalArgumentException("IO field set method must not be synthetic");
    }
    int m = method.getModifiers();
    if ((m & (Modifier.STATIC | Modifier.NATIVE | Modifier.VOLATILE)) != 0) {
      // Exclude static and native methods (but final methods are not excluded)
      throw new IllegalArgumentException("IO field set method must not be static, native or volatile");
    }

    String methodName = method.getName();
    if (methodName.startsWith("set") && methodName.length() > 3 && Character.isUpperCase(methodName.charAt(3))) {
      if (method.getParameterCount() != 1) {
        // Only one parameter is allowed if a set method identifies a form member.
        throw new IllegalArgumentException("IO field set method can only have 1 parameter");
      }
      Class<?> returnType = method.getReturnType();
      if (!returnType.getName().equals("void")) {
        // A setter method should return void
        throw new IllegalArgumentException("IO field set method must return void");
      }
      isSetter[0] = true;
      return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(3 + 1);
    } else {
      String fieldName;
      if (methodName.startsWith("get") && methodName.length() > 3 && Character.isUpperCase(methodName.charAt(3))) {
        fieldName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(3 + 1);
      } else if (methodName.startsWith("is") && methodName.length() > 2 && Character.isUpperCase(methodName.charAt(3))) {
        fieldName = Character.toLowerCase(methodName.charAt(2)) + methodName.substring(2 + 1);
      } else {
        fieldName = null;
      }
      if (fieldName != null) {
        // Its a getter method
        if (method.getParameterCount() != 0) {
          // No parameter is allowed if a get method identifies a form member.
          throw new IllegalArgumentException("IO field get method must have no parameters");
        }
        isSetter[0] = false;
        return fieldName;
      } else {
        throw new IllegalArgumentException("IO field method must be a get/is or set method");
      }
    }
  }
  
  
  private Method getMatchingGetter(Class<?> klass, String name) {
    String getterName = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    Method getter = null;
    try {
      getter = klass.getMethod(getterName);
      return getter;
    } catch (NoSuchMethodException ex) {
      // getter not found
    } catch (SecurityException ex) {
      throw new RuntimeException(ex);
    }
    getterName = "is" + getterName.substring(3);
    try {
      getter = klass.getMethod(getterName);
      return getter;
    } catch (NoSuchMethodException ex) {
      // getter not found
    } catch (SecurityException ex) {
      throw new RuntimeException(ex);
    }
    return null;
  }
 
  
  private Method getMatchingSetter(Class<?> klass, String name, Class<?> arg) {
    String setterName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
    Method setter = null;
    try {
      setter = klass.getMethod(setterName, arg);
      return setter;
    } catch (NoSuchMethodException ex) {
      // No setter found
    } catch (SecurityException ex) {
      throw new RuntimeException(ex);
    }
    return null;
  }
 
  
  private static boolean calcOptional (Class<?> memberType, MemberValueGetterSetter ioField) {
    if (memberType.isPrimitive()) {
      // Primitives cannot be optional
      return false;
    } else {
      // If an Optional annotation exists set the optional value.
      Optional optionalAnn = ioField.getAnnotation(Optional.class);
      if (optionalAnn != null) {
        return optionalAnn.value();
      } else {
        return false;
      }
    }
  }

  
  private void addClassFields2 (PlanFactory factory, Class<?> klass, Field[] fields, Method[] methods, boolean include) {
    // Parse the class hierarchy recursively
    Class<?> superKlass = klass.getSuperclass();
    if (superKlass != null && !superKlass.equals(Object.class)) {
      MappedSuperclass msc = superKlass.getAnnotation(MappedSuperclass.class);
      addClassFields(factory, superKlass, msc != null);
    }
    //Object defaultInstance = defaultInstance(klass);
    
    FieldDependency fieldDependency = new FieldDependency();
    fieldDependency.parseClass(klass.getName());

    if (include) {
//      Map<String, Field> lastEntryFields = new HashMap<String, Field>();

//      // Parse the declared fields of this class, first for 'last entry' fields
//      for (Field field : fields) {
//        if (field.isSynthetic()) {
//          // Synthetic fields cannot be form fields
//          continue;
//        }
//        int m = field.getModifiers();
//        if ((m & (Modifier.STATIC |Modifier.VOLATILE)) != 0) {
//          // Exclude static and volatile fields (but transient and final fields are not excluded)
//          continue;
//        }
//
//        // Remember the last entry fields
//        LastEntryFor lastEntryForAnn = field.getAnnotation(LastEntryFor.class);
//        if (lastEntryForAnn != null) {
//          for (String name : lastEntryForAnn.value()) {
//            lastEntryFields.put(name, field);
//          }
//          continue;
//        }
//        
//        // add last entry fields named by convention
//        String name = field.getName();
//        if (name.endsWith("LastEntry")) {
//          String n = name.substring(0, name.length() - 9);
//          lastEntryFields.put(n, field);
//          continue;
//        }
//      }

      // Look for fields with IOField or io field like annotation
      for (Field field : fields) {
        int modifiers = field.getModifiers();
        if ((modifiers & (Modifier.STATIC | Modifier.FINAL | Modifier.VOLATILE)) != 0) {
          // Note.  Transient fields are NOT excluded
          continue;
        }
        if (field.isAnnotationPresent(NotIOField.class)) {
          continue;
        }
        
        String name = field.getName();
        System.out.println("aaaaaaaaaaa " + name);
        
        Class<?> fieldType = field.getType();
        Type fieldGenericType = field.getGenericType();
        
        Method setter = getMatchingSetter(klass, name, fieldType);
        Method getter = getMatchingGetter(klass, name);
        MemberValueGetterSetter ioField = new MemberValueGetterSetter(name, field, setter, getter);
        
        // From here on in, all the fields are input fields.  It just depends on the
        // type as to whether the fields are embedded or not.
//        String label = null;
//        ItemField formFieldAnn = field.getAnnotation(ItemField.class);
//        if (formFieldAnn != null) {
//          label = formFieldAnn.label();
//        }
//        if (label == null || label.equals("\u0000")) {
//          label = CamelCase.toSentence(field.getName());
//        }
        
        EntryMode entryMode = EntryMode.UNSPECIFIED;
        Mode modeAnn = field.getAnnotation(Mode.class);
        if (modeAnn != null) {
          entryMode = modeAnn.value();
        }
        
        boolean optional = calcOptional(fieldType, ioField);

//        Field lastEntryField = lastEntryFields.get(field.getName());

        // Use the field value for setting up defaults only
        INodePlan nodePlan = NodePlanFactory.getNodePlan(factory, fieldGenericType, ioField, name, entryMode, 0, optional);
        System.out.println("SSSSS " + ioField.getName());
        System.out.println("SSSSS " + nodePlan.getName());
        memberPlans.put(name, nodePlan);
        memberFields.put(name, ioField);
        System.out.println("Aaaa " + memberPlans);
      }
      
      // Now look for methods with IOField annotation
      for (Method method : methods) {
        IOField ioFieldAnn = method.getAnnotation(IOField.class);
        if (ioFieldAnn == null) {
          continue;
        }
        
        boolean[] isSetter = new boolean[1];
        String name = isIOField(method, isSetter);
        
        if (memberPlans.containsKey(name)) {
          throw new IllegalArgumentException("IOField annotation on more than one field, get or set method: " + name);
        }
 
        Class<?> memberType;
        Type memberGenericType;
        MemberValueGetterSetter ioField;
        
        if (isSetter[0]) {
          memberType = method.getParameterTypes()[0];
          memberGenericType = method.getGenericParameterTypes()[0];
        
          Method setter = method;
          Method getter = getMatchingGetter(klass, name);
          ioField = new MemberValueGetterSetter(name, setter, getter, setter);
        } else {
          memberType = method.getReturnType();
          memberGenericType = method.getGenericReturnType();
          
          Method setter = getMatchingSetter(klass, name, memberType);
          Method getter = method;
          ioField = new MemberValueGetterSetter(name, setter, getter, getter);
        }
//        // Last entry fields are not form input fields
//        LastEntryFor lastEntryForAnn = field.getAnnotation(LastEntryFor.class);
//        if (lastEntryForAnn != null && lastEntryForAnn.value().length > 0) {
//          continue;
//        } else {
//          if (field.getName().endsWith("LastEntry")) {
//            continue;
//          }
//          // unless they are annotated with an empty LastEntryFor
//        }
        
        // From here on in, all the fields are input fields.  It just depends on the
        // type as to whether the fields are embedded or not.
//        String label = null;
//        ItemField formFieldAnn = field.getAnnotation(ItemField.class);
//        if (formFieldAnn != null) {
//          label = formFieldAnn.label();
//        }
//        if (label == null || label.equals("\u0000")) {
//          label = CamelCase.toSentence(field.getName());
//        }
        
        EntryMode entryMode = EntryMode.UNSPECIFIED;
        Mode modeAnn = method.getAnnotation(Mode.class);
        if (modeAnn != null) {
          entryMode = modeAnn.value();
        }
        
        boolean optional = calcOptional(memberType, ioField);

        INodePlan nodePlan = NodePlanFactory.getNodePlan(factory, memberGenericType, ioField, name, entryMode, 0, optional);
        memberPlans.put(name, nodePlan);
        memberFields.put(name, ioField);
      }
    }

    //findTypeForAnnotations (klass, fields, methods, fieldDependency);
    //findLabelForAnnotations (klass, fields, methods, fieldDependency);
    findModeForAnnotations (klass, fields, fieldDependency);
//    findImplementationForAnnotations (klass, fields, fieldDependency);
    findDefaultForAnnotations (klass, fields, methods, fieldDependency);
    findValuesForAnnotations (klass, fields, methods, fieldDependency);
    findOccursForAnnotations (klass, fields, fieldDependency);
    findFactoryForAnnotations (klass, fieldDependency);
    findValidationMethods (klass, fieldDependency);
  }
    
  
//  public static INodePlan buildObjectPlan (Field field, String name, String label, Type fieldType, int dimension, EntryMode entryMode, boolean optional) {
//    INodePlan objPlan;
//    
//    if (fieldType instanceof GenericArrayType) {
//      Type type1 = ((GenericArrayType)fieldType).getGenericComponentType();
//      objPlan = new ArrayPlan(field, name, label, (Class<?>)type1, dimension + 1, entryMode);
//    } else if (fieldType instanceof ParameterizedType) {
//      ParameterizedType ptype = (ParameterizedType)fieldType;
//      Type type1 = ptype.getRawType();
//      if (type1.equals(List.class)) {
//        Type[] typeArgs = ptype.getActualTypeArguments();
//        if (typeArgs.length != 1) {
//          throw new IllegalArgumentException("List must have one, and only one, type parameter");
//        }
//        Type type2 = typeArgs[0];
//        objPlan = new ListPlan(field, name, label, (Class<?>)type2, dimension + 1, entryMode);
//      } else {
//        throw new IllegalArgumentException("Parameterized type that is not a List");
//      }
//    } else if (fieldType instanceof Class) {
//      Class<?> klass = (Class<?>)fieldType;
//      if (klass.isArray()) {
//        Type type1 = klass.getComponentType();
//        objPlan = new ArrayPlan(field, name, label, (Class<?>)type1, dimension + 1, entryMode);
//      } else {
//        objPlan = fieldPlanDetail(field, name, label, fieldType, dimension, entryMode, optional);
//      }
//    } else {
//      throw new IllegalArgumentException("Unsupported type: " + fieldType);
//    }
//    return objPlan;
//  }
//
//  
//  @SuppressWarnings({ "unchecked", "rawtypes" })
//  static INodePlan fieldPlanDetail (Field field, String name, String label, Type fieldType, int dimension, EntryMode entryMode, boolean optional) {
//    INodePlan objectPlan;
//    
//    // Is there a type declaration within the class
//    Class<?> fieldClass = (Class<?>)fieldType;
//    ItemField itemFieldAnn = field.getAnnotation(ItemField.class);
//    
//    // Is there a named IType for the field (via type parameter of the FormField annotation),
//    // or does the field type match one of the build in field types
//    IType type = ItemTypeRegistry.lookupType(fieldClass, itemFieldAnn);
//    if (type != null) {
//      objectPlan = new ItemPlan(name, label, type, field, entryMode, optional);
//    } else {
//      // Is it a reference type (identified by the ManyToOne annotation).
//      ManyToOne fkAnn = field.getAnnotation(ManyToOne.class);
//      if (fkAnn != null) {
//        objectPlan = new ReferencePlan(field.getName(), label, fieldClass, entryMode, fkAnn.optional());
//      } else {
//        // A reference type can also be identified by the OneToOne annotation.
//        OneToOne fkAnn1 = field.getAnnotation(OneToOne.class);
//        if (fkAnn1 != null) {
//          objectPlan = new ReferencePlan(field.getName(), label, fieldClass, entryMode, fkAnn1.optional());
//        } else {
//          // Is it a class type (identified by the Embedded annotation.  The class is traversed and all
//          // members are considered as potential entry fields.
//          boolean embdAnn = field.isAnnotationPresent(Embedded.class);
//          if (embdAnn) {
//            objectPlan = new EmbeddedPlan(field.getName(), label, fieldClass, entryMode);
//          } else {
//            // The Embeddable annotation on the field class also identifies a class type.
//            boolean emblAnn = fieldClass.isAnnotationPresent(Embeddable.class);
//            if (emblAnn) {
//              objectPlan = new EmbeddedPlan(field.getName(), label, fieldClass, entryMode);
//            } else {
//              //If within a collection (array or list) any object that is not a field, is an embedded class type.
//              if (dimension >= 0) {
//                objectPlan = new EmbeddedPlan(field.getName(), label, fieldClass, entryMode);
//                //buildObjectPlan(field, field.getName(), fieldType, -1, entryMode, false);
//              } else {
//                // Otherwise, throw an error.
//                throw new RuntimeException("Field type not recognised: " + name + " " + fieldType);
//              }
//            }
//          }
//        }
//      }
//    }
//    return objectPlan;
//  }
// 
//   
//  public void walkClassPlan (Class<?> klass, WalkPlanTarget target) {
//    walkClassFields (klass, true, target);
//  }
//  
//  
//  private void walkClassFields (Class<?> klass, boolean include, WalkPlanTarget target) {
//    Field[] declaredFields = klass.getDeclaredFields();
//    walkClassFields2 (klass, declaredFields, include, target);
//  }
//  
//  
//  private void walkClassFields2 (Class<?> klass, Field[] fields, boolean include, WalkPlanTarget target) {
//    // Parse the class hierarchy recursively
//    Class<?> superKlass = klass.getSuperclass();
//    if (superKlass != null && !superKlass.equals(Object.class)) {
//      MappedSuperclass msc = superKlass.getAnnotation(MappedSuperclass.class);
//      walkClassFields(superKlass, msc != null, target);
//    }
//    
//    if (include) {
////      Map<String, Field> lastEntryFields = new HashMap<String, Field>();
//
////      // Parse the declared fields of this class, first for 'last entry' fields
////      for (Field field : fields) {
////        if (field.isSynthetic()) {
////          // Synthetic fields cannot be form fields
////          continue;
////        }
////        int m = field.getModifiers();
////        if ((m & (Modifier.STATIC |Modifier.VOLATILE)) != 0) {
////          // Exclude static and volatile fields (but transient and final fields are not excluded)
////          continue;
////        }
////
////        // Remember the last entry fields
////        LastEntryFor lastEntryForAnn = field.getAnnotation(LastEntryFor.class);
////        if (lastEntryForAnn != null) {
////          for (String name : lastEntryForAnn.value()) {
////            lastEntryFields.put(name, field);
////          }
////          continue;
////        }
////        
////        // add last entry fields named by convention
////        String name = field.getName();
////        if (name.endsWith("LastEntry")) {
////          String n = name.substring(0, name.length() - 9);
////          lastEntryFields.put(n, field);
////          continue;
////        }
////      }
//
//      // And again for the fields themselves
//      for (Field field : fields) {
//        if (field.isSynthetic()) {
//          // Synthetic fields cannot be form fields
//          continue;
//        }
//        int m = field.getModifiers();
//        if ((m & (Modifier.STATIC | Modifier.VOLATILE)) != 0) {
//          // Exclude static and volatile fields (but transient and final fields are not excluded)
//          continue;
//        }
//
//        NotFormField notFieldAnn = field.getAnnotation(NotFormField.class);
//        if (notFieldAnn != null) {
//          // Exclude fields annotated with @NotFormField
//          continue;
//        }
//        
////        // Last entry fields are not form input fields
////        LastEntryFor lastEntryForAnn = field.getAnnotation(LastEntryFor.class);
////        if (lastEntryForAnn != null && lastEntryForAnn.value().length > 0) {
////          continue;
////        } else {
////          if (field.getName().endsWith("LastEntry")) {
////            continue;
////          }
////          // unless they are annotated with an empty LastEntryFor
////        }
//        
//        // From here on in, all the fields are input fields.  It just depends on the
//        // type as to whether the fields are embedded or not.
//        EntryMode entryMode = EntryMode.UNSPECIFIED;
//        if ((m & Modifier.FINAL) != 0) {
//          entryMode = EntryMode.VIEW; 
//        }
//        Mode modeAnn = field.getAnnotation(Mode.class);
//        if (modeAnn != null) {
//          entryMode = modeAnn.value();
//          if ((m & Modifier.FINAL) != 0 && entryMode == EntryMode.ENTRY) {
//            throw new RuntimeException("Cannot set an entry mode of 'ENTRY' on final fields");
//          }
//        }
//        
//        boolean optional;
//        if (field.getDeclaringClass().isPrimitive()) {
//          // Primitives cannot be optional
//          optional = false;
//        } else {
//          // If an Optional annotation exists set the optional value.
//          Optional optionalAnn = field.getAnnotation(Optional.class);
//          if (optionalAnn != null) {
//            optional = optionalAnn.value();
//          } else {
//            optional = false;
//          }
//        }
//
////        Field lastEntryField = lastEntryFields.get(field.getName());
//
//        String name = field.getName();
//        
//        walkMemberPlan(name, field, -1, entryMode, optional, target);
//      }
//    }
//    
//    FieldDependency fieldDependency = new FieldDependency();
//    fieldDependency.parseClass(klass.getName());
//
//    findTypeForAnnotations (klass, fields, fieldDependency);
//    findLabelForAnnotations (klass, fields, fieldDependency);
//    findModeForAnnotations (klass, fields, fieldDependency);
//    findImplementationForAnnotations (klass, fields, fieldDependency);
//    findDefaultForAnnotations (klass, fields, fieldDependency);
//    findOccursForAnnotations (klass, fields, fieldDependency);
//    findFactoryForAnnotations (klass, fieldDependency);
//    findValidationMethods (klass, fieldDependency);
//  }
//      
//  
//  public static void walkMemberPlan (String name, Field field, int dimension, EntryMode entryMode, boolean optional, WalkPlanTarget target) {
//    Class<?> fieldClass = field.getType();
//    if (fieldClass.isArray()) {
//      Class<?> arrayClass = fieldClass.getComponentType();
//      target.array(name, arrayClass, dimension + 1, entryMode);
//    } else {
//      Type fieldType = field.getGenericType();
//      if (fieldType instanceof ParameterizedType) {
//        ParameterizedType ptype = (ParameterizedType)fieldType;
//        Type type1 = ptype.getRawType();
//        if (type1.equals(List.class)) {
//          Type[] typeArgs = ptype.getActualTypeArguments();
//          if (typeArgs.length != 1) {
//            throw new IllegalArgumentException("List must have one, and only one, type parameter");
//          }
//          Type type2 = typeArgs[0];
//          target.list(name, type2, field, dimension + 1, entryMode);
//        } else {
//          throw new IllegalArgumentException("Parameterized type that is not a List");
//        }
//      } else {
//        walkMemberPlanDetail(name, field, dimension, entryMode, optional, target);
//      }
//    }
//  }
//
//  
//   static void walkMemberPlanDetail (String name, Field field, int dimension, EntryMode entryMode, boolean optional, WalkPlanTarget target) {
//    // Is there a type declaration within the class
//    Class<?> fieldClass = field.getType();
//    ItemField formFieldAnn = field.getAnnotation(ItemField.class);
//    Column columnAnn = field.getAnnotation(Column.class);
//    
//    // Is there a named IType for the field (via type parameter of the FormField annotation),
//    // or does the field type match one of the build in field types
//    IType<?> type = BuiltinTypeRegistry.lookupType(fieldClass, formFieldAnn, columnAnn);
//    if (type != null) {
//      target.field(field.getName(), type, field, entryMode, optional);
//    } else {
//      // Is it a reference type (identified by the ManyToOne annotation).
//      ManyToOne fkAnn = field.getAnnotation(ManyToOne.class);
//      if (fkAnn != null) {
//        target.reference(field.getName(), fieldClass,  entryMode, fkAnn.optional());
//      } else {
//        // A reference type can also be identified by the OneToOne annotation.
//        OneToOne fkAnn1 = field.getAnnotation(OneToOne.class);
//        if (fkAnn1 != null) {
//          target.reference(field.getName(), fieldClass,  entryMode, fkAnn1.optional());
//        } else {
//          // Is it a class type (identified by the Embedded annotation.  The class is traversed and all
//          // members are considered as potential entry fields.
//          boolean embdAnn = field.isAnnotationPresent(Embedded.class);
//          if (embdAnn) {
//            target.embedded(field.getName(), fieldClass, entryMode);
//          } else {
//            // The Embeddable annotation on the field class also identifies a class type.
//            boolean emblAnn = fieldClass.isAnnotationPresent(Embeddable.class);
//            if (emblAnn) {
//              target.embedded(field.getName(), fieldClass, entryMode);
//            } else {
//              //If within a collection (array or list) any object that is not a field, is an embedded class type.
//              if (dimension >= 0) {
//                target.embedded(field.getName(), fieldClass, entryMode);
//              } else {
//                // Otherwise, throw an error.
//                throw new RuntimeException("Field type not recognised: " + name + " " + fieldClass);
//              }
//            }
//          }
//        }
//      }
//    }
//  }

  
//  private void findTypeForAnnotations (Class<?> klass, Field[] fields, Method[] methods, FieldDependency fieldDependency) {
//    // Look for fields annotated with TypeFor. 
//    for (Field field : fields) {
//      TypeFor typeFor = field.getAnnotation(TypeFor.class);
//      if (typeFor != null) {
//        // This field has been explicitly annotated as the type for some field or fields.
//        String[] xpaths = typeFor.value();
//        try {
//          int modifier = field.getModifiers();
//          if (Modifier.isStatic(modifier)) {
//            field.setAccessible(true);
//            IType<?> type = (IType<?>)field.get(null);
//            IRuntimeTypeProvider typeProvider = new RuntimeTypeProvider(type, xpaths);
//            runtimeTypeProviders.add(typeProvider);
//          }
//        } catch (IllegalArgumentException e) {
//          throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//          throw new RuntimeException(e);
//        }
//      }
//    }
//
//    // Look for methods annotated with TypeFor. 
//    for (Method method : methods) {
//      TypeFor typeFor = method.getAnnotation(TypeFor.class);
//      if (typeFor != null) {
//        // This method has been explicitly annotated as the type for some field or fields.
//        String[] xpaths = typeFor.value();
//        try {
//          int modifier = method.getModifiers();
//          if (Modifier.isStatic(modifier)) {
//            method.setAccessible(true);
//            IType<?> type = (IType<?>)method.invoke(null);
//            IRuntimeTypeProvider typeProvider = new RuntimeTypeProvider(type, xpaths);
//            runtimeTypeProviders.add(typeProvider);
//          } else {
//            IRuntimeTypeProvider typeProvider = new RuntimeTypeProvider(klass, fieldDependency, method, xpaths);
//            runtimeTypeProviders.add(typeProvider);
//          }
//        } catch (InvocationTargetException e) {
//          throw new RuntimeException(e);
//        } catch (IllegalArgumentException e) {
//          throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//          throw new RuntimeException(e);
//        }
//      }
//    }
//  }
  
  
  private void findModeForAnnotations (Class<?> klass, Field[] fields, FieldDependency fieldDependency) {
    // Look for fields annotated with ModeFor. 
    for (Field field : fields) {
      ModeFor modeFor = field.getAnnotation(ModeFor.class);
      if (modeFor != null) {
        // This field has been explicitly annotated as an mode for some field or fields.
        String[] xpaths = modeFor.value();
        try {
          int modifier = field.getModifiers();
          if (Modifier.isStatic(modifier)) {
            field.setAccessible(true);
            EntryMode mode = (EntryMode)field.get(null);
            IRuntimeModeProvider modeProvider = new RuntimeModeProvider(mode, xpaths);
            runtimeModeProviders.add(modeProvider);
          }
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
    
    // Look for methods annotated with ModeFor. 
    for (Method method : klass.getDeclaredMethods()) {
      ModeFor modeFor = method.getAnnotation(ModeFor.class);
      if (modeFor != null) {
        // This method has been explicitly annotated as the use for some field or fields.
        String[] xpaths = modeFor.value();
        try {
          int modifier = method.getModifiers();
          if (Modifier.isStatic(modifier)) {
            method.setAccessible(true);
            EntryMode mode = (EntryMode)method.invoke(null);
            IRuntimeModeProvider modeProvider = new RuntimeModeProvider(mode, xpaths);
            runtimeModeProviders.add(modeProvider);
          } else {
            IRuntimeModeProvider modeProvider = new RuntimeModeProvider(klass, fieldDependency, method, xpaths);
            runtimeModeProviders.add(modeProvider);
          }
        } catch (InvocationTargetException e) {
          throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
  
  
//  private void findImplementationForAnnotations (Class<?> klass, Field[] fields, FieldDependency fieldDependency) {
//    // Look for fields annotated with ImplementationFor. 
//    for (Field field : fields) {
//      ImplementationFor modeFor = field.getAnnotation(ImplementationFor.class);
//      if (modeFor != null) {
//        // This field has been explicitly annotated as an mode for some field or fields.
//        String[] xpaths = modeFor.value();
//        try {
//          int modifier = field.getModifiers();
//          if (Modifier.isStatic(modifier)) {
//            field.setAccessible(true);
//            Class<?> implClass = (Class<?>)field.get(null);
//            IRuntimeImplementationProvider implProvider = new RuntimeImplementationProvider(implClass, xpaths);
//            runtimeImplementationProviders.add(implProvider);
//          }
//        } catch (IllegalArgumentException e) {
//          throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//          throw new RuntimeException(e);
//        }
//      }
//    }
//    
//    // Look for methods annotated with ImplementationFor. 
//    for (Method method : klass.getDeclaredMethods()) {
//      ImplementationFor modeFor = method.getAnnotation(ImplementationFor.class);
//      if (modeFor != null) {
//        // This method has been explicitly annotated as the use for some field or fields.
//        String[] xpaths = modeFor.value();
//        try {
//          int modifier = method.getModifiers();
//          if (Modifier.isStatic(modifier)) {
//            method.setAccessible(true);
//            Class<?> implClass = (Class<?>)method.invoke(null);
//            IRuntimeImplementationProvider implProvider = new RuntimeImplementationProvider(implClass, xpaths);
//            runtimeImplementationProviders.add(implProvider);
//          } else {
//            IRuntimeImplementationProvider implProvider = new RuntimeImplementationProvider(klass, fieldDependency, method, xpaths);
//            runtimeImplementationProviders.add(implProvider);
//          }
//        } catch (InvocationTargetException e) {
//          throw new RuntimeException(e);
//        } catch (IllegalArgumentException e) {
//          throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//          throw new RuntimeException(e);
//        }
//      }
//    }
//  }
  
  
  private void findOccursForAnnotations (Class<?> klass, Field[] fields, FieldDependency fieldDependency) {
    // Look for fields annotated with OccursFor. 
    for (Field field : fields) {
      OccursFor occursFor = field.getAnnotation(OccursFor.class);
      if (occursFor != null) {
        // This field has been explicitly annotated as the size of an array field or fields.
        String[] appliesTo = occursFor.value();
        try {
          int modifier = field.getModifiers();
          if (Modifier.isStatic(modifier)) {
            field.setAccessible(true);
            int size = (Integer)field.get(null);
            IRuntimeOccursProvider occursProvider = new RuntimeOccursProvider(size, appliesTo);
            runtimeOccursProviders.add(occursProvider);
          } else {
            IRuntimeOccursProvider occursProvider = new RuntimeOccursProvider(klass, fieldDependency, field, appliesTo);
            runtimeOccursProviders.add(occursProvider);
          }
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
    
    // Look for methods annotated with OccursFor. 
    for (Method method : klass.getDeclaredMethods()) {
      OccursFor occursFor = method.getAnnotation(OccursFor.class);
      if (occursFor != null) {
        // This method has been explicitly annotated as the size of an array field or fields.
        String[] xpaths = occursFor.value();
        try {
          int modifier = method.getModifiers();
          if (Modifier.isStatic(modifier)) {
            method.setAccessible(true);
            int size = (Integer)method.invoke(null);
            IRuntimeOccursProvider occursProvider = new RuntimeOccursProvider(size, xpaths);
            runtimeOccursProviders.add(occursProvider);
          } else {
            IRuntimeOccursProvider occursProvider = new RuntimeOccursProvider(klass, fieldDependency, method, xpaths);
            runtimeOccursProviders.add(occursProvider);
          }
        } catch (InvocationTargetException e) {
          throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
  
  
//  private void findLabelForAnnotations (Class<?> klass, Field[] fields, Method[] methods, FieldDependency fieldDependency) {
//    // Look for fields annotated with LabelFor. 
//    for (Field field : fields) {
//      LabelFor labelFor = field.getAnnotation(LabelFor.class);
//      if (labelFor != null) {
//        // This field has been explicitly annotated as a label for some field or fields.
//        String[] xpaths = labelFor.value();
//        try {
//          int modifier = field.getModifiers();
//          if (Modifier.isStatic(modifier)) {
//            field.setAccessible(true);
//            String label = (String)field.get(null);
//            IRuntimeLabelProvider labelProvider = new RuntimeLabelProvider(label, xpaths);
//            runtimeLabelProviders.add(labelProvider);
//          }
//        } catch (IllegalArgumentException e) {
//          throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//          throw new RuntimeException(e);
//        }
//      }
//    }
//    
//    // Look for methods annotated with LabelFor. 
//    for (Method method : methods) {
//      LabelFor labelFor = method.getAnnotation(LabelFor.class);
//      if (labelFor != null) {
//        // This method has been explicitly annotated as the label for some field or fields.
//        String[] xpaths = labelFor.value();
//        try {
//          int modifier = method.getModifiers();
//          if (Modifier.isStatic(modifier)) {
//            method.setAccessible(true);
//            String label = (String)method.invoke(null);
//            IRuntimeLabelProvider labelProvider = new RuntimeLabelProvider(label, xpaths);
//            runtimeLabelProviders.add(labelProvider);
//          } else {
//            IRuntimeLabelProvider labelProvider = new RuntimeLabelProvider(klass, fieldDependency, method, xpaths);
//            runtimeLabelProviders.add(labelProvider);
//          }
//        } catch (InvocationTargetException e) {
//          throw new RuntimeException(e);
//        } catch (IllegalArgumentException e) {
//          throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//          throw new RuntimeException(e);
//        }
//      }
//    }
//    
//  }
  
  
  // The standard for all findXxxxxForAnnotations
  private void findDefaultForAnnotations (Class<?> classClass, Field[] fields, Method[] methods, FieldDependency fieldDependency) {
    // Look for fields annotated with DefaultFor. 
    for (Field field : fields) {
      DefaultFor defaultFor = field.getAnnotation(DefaultFor.class);
      if (defaultFor != null) {
        // This field has been explicitly annotated as the default for some field or fields.
        String[] xpaths = defaultFor.value();
        try {
          int modifier = field.getModifiers();
          if (Modifier.isStatic(modifier)) {
            field.setAccessible(true);
            Object defaultValue = field.get(null);
            IRuntimeDefaultProvider defaultProvider = new RuntimeDefaultProvider(defaultValue, xpaths);
            runtimeDefaultProviders.add(defaultProvider);
          } else {
            IRuntimeDefaultProvider defaultProvider = new RuntimeDefaultProvider(classClass, fieldDependency, field, xpaths);
            runtimeDefaultProviders.add(defaultProvider);
          }
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }

    // Look for methods annotated with DefaultFor. 
    for (Method method : classClass.getDeclaredMethods()) {
      DefaultFor defaultFor = method.getAnnotation(DefaultFor.class);
      if (defaultFor != null) {
        // This method has been explicitly annotated as the default for some field or fields.
        String[] xpaths = defaultFor.value();
        try {
          int modifier = method.getModifiers();
          if (Modifier.isStatic(modifier)) {
            method.setAccessible(true);
            Object defaultValue = method.invoke(null);
            IRuntimeDefaultProvider defaultProvider = new RuntimeDefaultProvider(defaultValue, xpaths);
            runtimeDefaultProviders.add(defaultProvider);
          } else {
//            Class<?>[] methodParams = method.getParameterTypes();
//            boolean isIndex;
//            if (methodParams.length == 0) {
//              isIndex = false;
//            } else if (methodParams.length == 1 && methodParams[0] == Integer.TYPE) {
//              isIndex = true;
//            } else {
//              throw new RuntimeException("DefaultFor method must have no parameters or a single int parameter");
//            }
            IRuntimeDefaultProvider defaultProvider = new RuntimeDefaultProvider(classClass, fieldDependency, method, xpaths);
            runtimeDefaultProviders.add(defaultProvider);
          }
        } catch (InvocationTargetException e) {
          throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
  
  
  @SuppressWarnings("unchecked")
  private void findValuesForAnnotations (Class<?> classClass, Field[] fields, Method[] methods, FieldDependency fieldDependency) {
    // Look for fields annotated with ValuesFor. 
    for (Field field : fields) {
      ValuesFor valuesFor = field.getAnnotation(ValuesFor.class);
      if (valuesFor != null) {
        // This field has been explicitly annotated as the values for target fields.
        String[] xpaths = valuesFor.value();
        try {
          int modifier = field.getModifiers();
          if (Modifier.isStatic(modifier)) {
            field.setAccessible(true);
            List<ICode> staticCodeValues = (List<ICode>)field.get(null);
            IRuntimeValuesProvider valuesProvider = new RuntimeValuesProvider(staticCodeValues, xpaths);
            runtimeValuesProviders.add(valuesProvider);
          }
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }

    // Look for methods annotated with DefaultFor. 
    for (Method method : classClass.getDeclaredMethods()) {
      ValuesFor valuesFor = method.getAnnotation(ValuesFor.class);
      if (valuesFor != null) {
        // This method has been explicitly annotated as the default for some field or fields.
        String[] fieldNames = valuesFor.value();
        try {
          int modifier = method.getModifiers();
          if (Modifier.isStatic(modifier)) {
            method.setAccessible(true);
            List<ICode> staticCodeValues = (List<ICode>)method.invoke(null);
            IRuntimeValuesProvider valuesProvider = new RuntimeValuesProvider(staticCodeValues, fieldNames);
            runtimeValuesProviders.add(valuesProvider);
          } else {
//            Class<?>[] methodParams = method.getParameterTypes();
//            boolean isIndex;
//            if (methodParams.length == 0) {
//              isIndex = false;
//            } else if (methodParams.length == 1 && methodParams[0] == Integer.TYPE) {
//              isIndex = true;
//            } else {
//              throw new RuntimeException("DefaultFor method must have no parameters or a single int parameter");
//            }
            IRuntimeValuesProvider valuesProvider = new RuntimeValuesProvider(classClass, fieldDependency, method, fieldNames);
            runtimeValuesProviders.add(valuesProvider);
          }
        } catch (InvocationTargetException e) {
          throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
  
  
  // Checked to match findDefaultForAnnotations
  private void findFactoryForAnnotations (Class<?> classClass, FieldDependency fieldDependency) {
    // Look for methods annotated with FactoryFor. 
    for (Method method : classClass.getDeclaredMethods()) {
      FactoryFor factoryFor = method.getAnnotation(FactoryFor.class);
      if (factoryFor != null) {
        // This method has been explicitly annotated as the variant for some field or fields.
        String[] xpaths = factoryFor.value();
        try {
          int modifier = method.getModifiers();
          if (Modifier.isStatic(modifier)) {
            method.setAccessible(true);
            Object implementationValue = method.invoke(null);
            IRuntimeFactoryProvider factoryProvider = new RuntimeFactoryProvider(implementationValue, xpaths);
            runtimeFactoryProviders.add(factoryProvider);
          } else {
            IRuntimeFactoryProvider factoryProvider = new RuntimeFactoryProvider(classClass, fieldDependency, method, xpaths);
            runtimeFactoryProviders.add(factoryProvider);
          }
        } catch (InvocationTargetException e) {
          throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
          throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
  
  
  private void findValidationMethods (Class<?> classClass, FieldDependency fieldDependency) {
    for (Method method : classClass.getDeclaredMethods()) {
      // Consider only methods with zero parameters, a void return type and non-static.
      if (method.getParameterTypes().length == 0 &&
          method.getReturnType().equals(Void.TYPE) &&
          !Modifier.isStatic(method.getModifiers())) {
        Validation validation = method.getAnnotation(Validation.class);
        if (validation != null) {
          boolean isSlow = validation.slow();
          if (throwsException(method)) {
            IValidationMethod validationMethod = new ValidationMethod(classClass, fieldDependency, method, isSlow);
            validationMethods.add(validationMethod);
          } else {
            throw new RuntimeException("Method with Validation annotation, but does not throw Exception");
          }
        } else {
          if (throwsUserEntryException(method)) {
            IValidationMethod validationMethod = new ValidationMethod(classClass, fieldDependency, method, false);
            validationMethods.add(validationMethod);
          }
        }
      }
    }
  }


  private static boolean throwsException (Method method) {
    Class<?>[] exceptions = method.getExceptionTypes();
    for (Class<?> ex : exceptions) {
      if (Exception.class.isAssignableFrom(ex)) {
        return true;
      }
    }
    return false;
  }

  
  private boolean throwsUserEntryException (Method method) {
    Class<?>[] exceptions = method.getExceptionTypes();
    for (Class<?> ex : exceptions) {
      if (UserEntryException.class.isAssignableFrom(ex)) {
        return true;
      }
    }
    return false;
  }

  
//  @Override
//  public List<IRuntimeLabelProvider> getRuntimeLabelProviders() {
//    return runtimeLabelProviders;
//  }

  
  @Override
  public List<IRuntimeModeProvider> getRuntimeModeProviders() {
    return runtimeModeProviders;
  }

  
//  @Override
//  public List<IRuntimeImplementationProvider> getRuntimeImplementationProviders() {
//    return runtimeImplementationProviders;
//  }

  
  @Override
  public List<IRuntimeDefaultProvider> getRuntimeDefaultProviders() {
    return runtimeDefaultProviders;
  }

  
  @Override
  public List<IRuntimeValuesProvider> getRuntimeValuesProviders() {
    return runtimeValuesProviders;
  }

  
  @Override
  public List<IRuntimeFactoryProvider> getRuntimeFactoryProviders() {
    return runtimeFactoryProviders;
  }

  
//  public List<IRuntimeFactoryProvider2> getRuntimeFactoryProviders2() {
//    return runtimeFactoryProviders2;
//  }

  
//  @Override
//  public List<IRuntimeTypeProvider> getRuntimeTypeProviders() {
//    return runtimeTypeProviders;
//  }

  
  @Override
  public Set<IValidationMethod> getValidationMethods() {
    return validationMethods;
  }

  
  @Override
  public List<IRuntimeOccursProvider> getRuntimeOccursProviders() {
    return runtimeOccursProviders;
  }

  
  @SuppressWarnings("unchecked")
  @Override
  public <X extends INodePlan> X getMember(String name) {
    return (X)memberPlans.get(name);
  }


  @SuppressWarnings("unchecked")
  @Override
  public INode getNameMappedNode(String name) {
    return memberPlans.get(name);
  }


  @Override
  public INodePlan[] getMembers() {
    INodePlan[] mx = new INodePlan[memberPlans.size()];
    int i = 0;
    for (INodePlan m : memberPlans.values()) {
      mx[i++] = m;
    }
    return mx;
  }

  
  @Override
  public MemberValueGetterSetter getNodeField (String memberName) {
    return memberFields.get(memberName);
  }
  
  
  @Override
  public void indent (int level) {
    for (int i = 0; i < level; i++) {
      System.out.print("  ");
    }
  }
  
  
  @Override
  public void dump (int level) {
    indent(level);
    System.out.println("NameMappedPlan(" + klass.getName() + "[" + memberPlans.size() + "]," + super.toString() + ")");
    for (IRuntimeFactoryProvider factoryProvider : runtimeFactoryProviders) {
      indent(level + 1);
      System.out.println(factoryProvider);
    }
    for (Map.Entry<String, INodePlan> entry : memberPlans.entrySet()) {
      indent(level+ 1);
      System.out.println(entry.getKey() + ":");
      INodePlan member = entry.getValue();
      member.dump(level + 2);
    }
  }


  @Override
  public Class<?> getSourceClass() {
    return klass;
  }


  @SuppressWarnings("unchecked")
  @Override
  public <X> X newInstance () {
    Object instance;
    try {
      instance = klass.newInstance();
    } catch (InstantiationException | IllegalAccessException ex) {
      throw new RuntimeException(ex);
    }
    return (X)instance;  
  }
  
  
  @SuppressWarnings("unchecked")
  @Override
  public Object newInstance (Object fromInstance) {
    Object toInstance = newInstance();
    for (INodePlan member : memberPlans.values()) {
      Object fromValue = member.getFieldValue(fromInstance);

      if (member instanceof IItemPlan) {
        member.setFieldValue(toInstance, fromValue);      
      } else if (member instanceof IContainerPlan) {
        Object newValue = ((IContainerPlan)member).newInstance(fromValue);
        member.setFieldValue(toInstance, newValue);      
      } else {
        throw new RuntimeException("Non-supported node plan: " + member);
      }
    }
    return toInstance;
  }


  @Override
  public Collection<? extends INode> getContainerNodes() {
    return memberPlans.values();
  }


//  @Override
//  public void accumulateTopItemPlans(List<IItemPlan<?>> itemPlans) {
//    for (INodePlan nodePlan : memberPlans.values()) {
//      nodePlan.accumulateTopItemPlans(itemPlans);
//    }
//  }


//  @Override
//  public Object getMemberValue(Object instance, String name) {
//    Field field = memberFields.get(name);
//    Object value;
//    try {
//      field.setAccessible(true);
//      value = field.get(instance);
//    } catch (IllegalArgumentException | IllegalAccessException ex) {
//      throw new RuntimeException(ex);
//    }
//    return value;
//  }
//
//
//  @Override
//  public void setMemberValue(Object instance, String name, Object value) {
//    Field field = memberFields.get(name);
//    try {
//      field.setAccessible(true);
//      field.set(instance, value);
//    } catch (IllegalArgumentException | IllegalAccessException ex) {
//      throw new RuntimeException(ex);
//    }
//  }


//  @Override
//  public IObjectModel buildModel(IForm<?> form, IContainerReference container) {
//    return new ClassModel(form, container, this);
//  }


//  @Override
//  public T newInstance() {
//    T instance;
//    try {
//      instance = klass.newInstance();
//    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
//      throw new RuntimeException(ex);
//    }
//    return instance;
//  }

}
