package org.plcore.userio.desc;

import java.lang.reflect.Field;

public class DescriptionFactory {

  public static String getDescription(Object instance) {    
    // Use the entity's self describing method if present
    if (instance instanceof SelfDescribing) {
      SelfDescribing describing = (SelfDescribing)instance;
      return describing.elicitDescription();
    }
    
    // Otherwise, concatenate all top level nodes that are marked as describing.
    String description = null;
    Class<?> klass = instance.getClass();
    Field[] fields = klass.getDeclaredFields();
    int i = 0;
    for (Field field : fields) {
      if (field.isAnnotationPresent(Describing.class)) {
        field.setAccessible(true);
        String part;
        try {
          part = field.get(instance).toString();
        } catch (IllegalArgumentException | IllegalAccessException ex) {
          throw new RuntimeException(ex);
        }
        if (i == 0) {
          description = part;
        } else {
          description += " " + part;
        }
        i++;
      }
    }
    if (description != null) {
      return description;
    }
    
    // Otherwise, use the first top level String field
    for (Field field : fields) {
      Class<?> klass2 = field.getType();
      if (String.class.isAssignableFrom(klass2)) {
        field.setAccessible(true);
        try {
          return (String)field.get(instance);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
          throw new RuntimeException(ex);
        }
      }
    }

    // Otherwise, return an empty description
    return "";
  }
}
