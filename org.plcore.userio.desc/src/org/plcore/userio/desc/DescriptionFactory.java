package org.plcore.userio.desc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DescriptionFactory {
  
  private boolean isExplicit = false;
  
  private List<Field> describingFields;
  
  public DescriptionFactory (Class<?> klass) {
    if (IExplicitDescription.class.isAssignableFrom(klass)) {
      isExplicit = true;
    } else {
      describingFields = new ArrayList<>();
      Field[] fields = klass.getDeclaredFields();
      for (Field field : fields) {
        if (field.isAnnotationPresent(Describing.class)) {
          describingFields.add(field);
        }
      }
      if (describingFields.size() == 0) {
        // Otherwise, use the first top level String field
        for (Field field : fields) {
          Class<?> klass2 = field.getType();
          if (String.class.isAssignableFrom(klass2)) {
            describingFields.add(field);
            break;
          }
        }
      }
    }
  }

  
  public String getDescription(Object instance) {    
    // Use the entity's self describing method if present
    if (isExplicit) {
      IExplicitDescription describing = (IExplicitDescription)instance;
      return describing.getDescription();
    }
    
    // Otherwise, concatenate all describing fields (or first text field).
    String description = "";
    int i = 0;
    for (Field field : describingFields) {
      field.setAccessible(true);
      try {
        if (i == 0) {
          description = field.get(instance).toString();
        } else {
          description += " " + field.get(instance);
        }
      } catch (IllegalArgumentException | IllegalAccessException ex) {
        throw new RuntimeException(ex);
      }
      i++;
    }
    return description;    
  }
}
