package org.plcore.userio.plan;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MemberValueGetterSetter {

  private final String name;
  private final Field field;
  private final Method setter;
  private final Method getter;
  private final Method annotatedMethod;
  
  
  public MemberValueGetterSetter(String name, Field field, Method setter, Method getter) {
    this.name = name;
    if (field == null) {
      throw new IllegalArgumentException("field may not be null");
    }
    this.field = field;
    this.field.setAccessible(true);
    this.setter = setter;
    this.getter = getter;
    this.annotatedMethod = null;
  }
  
  
  public MemberValueGetterSetter(String name, Method getter) {
    this.name = name;
    this.field = null;
    this.setter = null;
    this.getter = getter;
    if (getter == null) {
      throw new IllegalArgumentException("getter may not be null");
    }
    this.getter.setAccessible(true);
    this.annotatedMethod = getter;
  }
  
  
  public MemberValueGetterSetter(String name, Method setter, Method getter, Method annotatedMethod) {
    this.name = name;
    this.field = null;
    this.setter = setter;
    this.getter = getter;
    if (annotatedMethod == null) {
      throw new IllegalArgumentException("annotatedMethod may not be null");
    }
    this.annotatedMethod = annotatedMethod;
    
    this.setter.setAccessible(true);
    this.getter.setAccessible(true);
  }
  
  
//  public Method getGetter() {
//    return getter;
//  }
  
  
  public Class<?> getDeclaringClass() {
    if (field != null) {
      return field.getDeclaringClass();
    } else {
      return annotatedMethod.getDeclaringClass();
    }
  }
  
  
  public boolean isSettable() {
    return field != null || setter != null;
  }
  
  
  public String getName() {
    return name;
  }
  
  
  @SuppressWarnings("unchecked")
  public <X> X get(Object obj) {
    try {
      if (getter != null) {
        return (X)getter.invoke(obj);
      } else if (field != null) {
        return (X)field.get(obj);
      } else { 
        throw new RuntimeException(name + ": No get method or field for 'get' operation");
      }
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  public void set(Object obj, Object value) {
    try {
      if (setter != null) {
        setter.invoke(obj, value);
      } else if (field != null) {
        field.set(obj, value);
      } else {
        throw new IllegalStateException(name + ": No set method or field for 'set' operation");
      }
    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  public <A extends Annotation> A getAnnotation(Class<A> klass) {
    if (field != null) {
      return field.getAnnotation(klass);
    } else {
      return annotatedMethod.getAnnotation(klass);
    }
  }
  
  
  public boolean isAnnotationPresent(Class<? extends Annotation> klass) {
    if (field != null) {
      return field.isAnnotationPresent(klass);
    } else {
      return annotatedMethod.isAnnotationPresent(klass);
    }
  }
  
}
