package org.plcore.osgi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.regex.Pattern;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.type.IType;
import org.plcore.type.TypeRegistry;
import org.plcore.type.UserEntryException;
import org.plcore.value.ExistingDirectory;


@Component(service = ConfigurationLoader.class)
public class ConfigurationLoader {

  @Reference
  private TypeRegistry typeRegistry;
  
  
  public void load(Object target, ComponentContext context) {
    try {
      Dictionary<String, Object> dict = context.getProperties();
      
      Class<?> klass = target.getClass();
      Field[] fields = klass.getDeclaredFields();
      for (Field field : fields) {
        Configurable configAnn = field.getAnnotation(Configurable.class);
        if (configAnn != null) {
          String propertyName = configAnn.name();
          if (propertyName.length() == 0) {
            propertyName = field.getName();
          }
          Class<?> fieldClass = field.getType();
          if (fieldClass.isArray()) {
            ArrayList<Object> list = new ArrayList<>();
            String prefix = propertyName + ".";

            for (Enumeration<?> e = dict.keys(); e.hasMoreElements(); ) {
              String key = (String)e.nextElement();
              if (key.equals(propertyName) || key.startsWith(prefix)) {
                String propertyValue = dict.get(key).toString();
                Object fieldValue = getFieldValue(field.getType(), field.getName(), propertyValue);
                list.add(fieldValue);
              }
            }
            Object[] array = list.toArray();
            field.setAccessible(true);
            field.set(target, array);
          } else {
            Object propertyValue = dict.get(propertyName);
            if (propertyValue != null) {
              Object fieldValue = getFieldValue(field.getType(), field.getName(), propertyValue.toString());
              field.setAccessible(true);
              field.set(target, fieldValue);
            } else if (configAnn.required()) {
              for (Enumeration<?> e = dict.keys(); e.hasMoreElements(); ) {
                String key = (String)e.nextElement();
                Object value = dict.get(key);
                System.out.println("Dictionary: " + key + " = " + value);
              }
              throw new IllegalConfigurationException(
                  "Configuration value '" + propertyName + "' required for " + klass.getSimpleName());
            }
          }
        }
      }
    } catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException
           | InvocationTargetException | ClassNotFoundException | UserEntryException ex) {
      throw new RuntimeException(ex);
    }
  }


  private Object getFieldValue(Class<?> fieldClass, String fieldName, String propertyValue) throws InstantiationException,
                               IllegalAccessException, IllegalArgumentException, 
                               InvocationTargetException, ClassNotFoundException,
                               UserEntryException {
    Object value;

    if (fieldClass.isEnum()) {
      value = getPropertyAsEnum(propertyValue, fieldClass);
    } else {
      IType<?> type = typeRegistry.getByFieldClass(fieldClass, fieldName);
      if (type != null) {
        try {
          value = type.createFromString(propertyValue);
        } catch (UserEntryException ex) {
          throw new RuntimeException("'" + propertyValue + "' " + ex.getMessage());
        }
      } else if (ExistingDirectory.class.isAssignableFrom(fieldClass)) {
        value = new ExistingDirectory(Paths.get(propertyValue));
      } else if (Path.class.isAssignableFrom(fieldClass)) {
        value = Paths.get(propertyValue);
      } else if (Pattern.class.isAssignableFrom(fieldClass)) {
        value = Pattern.compile(propertyValue);
      } else if (Class.class.isAssignableFrom(fieldClass)) {
        value = Class.forName(propertyValue);
      } else {
        try {
          Constructor<?> constructor = fieldClass.getDeclaredConstructor(String.class);
          constructor.setAccessible(true);
          value = constructor.newInstance(propertyValue);
        } catch (NoSuchMethodException ex) {
          if (String.class.isAssignableFrom(fieldClass)) {
            value = (String)propertyValue;
          } else {
            throw new RuntimeException("Unsupported data type: " + fieldClass.getCanonicalName());
          }
        }
      }
    }
    return value;
  }


  private static <E> E getPropertyAsEnum(String property, Class<E> enumClass) {
    E[] values = enumClass.getEnumConstants();
    for (E value : values) {
      if (value.toString().equals(property)) {
        return value;
      }
    }
    try {
      int i = Integer.parseInt(property);
      if (i >= 0 && i < values.length) {
        return values[i];
      } else {
        throw new IllegalArgumentException(property);
      }
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException(property);
    }
  }

}
