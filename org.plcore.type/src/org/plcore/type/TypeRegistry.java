package org.plcore.type;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.plcore.type.builtin.CodeType;
import org.plcore.type.builtin.EnumType;
import org.plcore.value.ICode;


@Component(service = TypeRegistry.class, immediate = true)
public class TypeRegistry {

  private Map<String, IType<?>> namedTypes = new HashMap<>();
  private Map<Class<?>, IType<?>> inferredTypes = new HashMap<>();


  @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  protected void addType(IType<?> type, Map<String, Object> props) {
    synchronized (namedTypes) {
      synchronized (inferredTypes) {
        String name = (String)props.get("name");
        if (name == null) {
          name = (String)props.get("component.name");
        }
        IType<?> oldType = namedTypes.putIfAbsent(name, type);
        if (oldType != null) {
          // This error should never happen
          throw new RuntimeException("Duplicate IType with the same component.name: " + name);
        }

        Class<?> fieldClass = type.getFieldClass();
        oldType = inferredTypes.putIfAbsent(fieldClass, type);
        if (oldType != null) {
          throw new RuntimeException("Duplicate IType with the same target class: " + fieldClass.getSimpleName());
        }
        inferredTypes.put(fieldClass, type);
      }
    }
  }


  protected synchronized void removeType(IType<?> type, Map<String, Object> props) {
    synchronized (namedTypes) {
      synchronized (inferredTypes) {
        String name = (String)props.get("name");
        if (name == null) {
          name = (String)props.get("component.name");
        }
        if (name != null) {
          namedTypes.remove(name);
        }

        Class<?> fieldClass = type.getFieldClass();
        inferredTypes.remove(fieldClass);
      }
    }
  }


  // static {
  // typeMap.put(BigDecimal.class, BigDecimalType.class);
  // typeMap.put(BigInteger.class, BigIntegerType.class);
  // typeMap.put(Boolean.class, BooleanType.class);
  // typeMap.put(Boolean.TYPE, BooleanType.class);
  // typeMap.put(Byte.class, ByteType.class);
  // typeMap.put(Byte.TYPE, ByteType.class);
  // typeMap.put(Character.class, CharacterType.class);
  // typeMap.put(Character.TYPE, CharacterType.class);
  // typeMap.put(Date.class, DateType.class);
  // typeMap.put(Decimal.class, DecimalType.class);
  // // entries.put(Directory.class, DirectoryType.class);
  // typeMap.put(Double.class, DoubleType.class);
  // typeMap.put(Double.TYPE, DoubleType.class);
  // // entries.put(EmailAddress.class, EmailAddressType.class);
  // typeMap.put(EntityLife.class, EntityLifeType.class);
  // typeMap.put(FileContent.class, FileContentType.class);
  // typeMap.put(File.class, PathType.class);
  // typeMap.put(Float.class, FloatType.class);
  // typeMap.put(Float.TYPE, FloatType.class);
  // // entries.put(ImageCode.class, ImageCodeType.class);
  // typeMap.put(Integer.class, IntegerType.class);
  // typeMap.put(Integer.TYPE, IntegerType.class);
  // typeMap.put(LocalDate.class, LocalDateType.class);
  // typeMap.put(Long.class, LongType.class);
  // typeMap.put(Long.TYPE, LongType.class);
  // // entries.put(Password.class, PasswordType.class);
  // // entries.put(PhoneNumber.class, PhoneNumberType.class);
  // typeMap.put(Short.class, ShortType.class);
  // typeMap.put(Short.TYPE, ShortType.class);
  // typeMap.put(java.sql.Date.class, SqlDateType.class);
  // typeMap.put(String.class, StringType.class);
  // typeMap.put(VersionTime.class, VersionTimeType.class);
  // typeMap.put(URL.class, URLType.class);
  // }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public IType<?> getType(Class<?> nodeClass) {
    synchronized (inferredTypes) {
      IType<?> type = inferredTypes.get(nodeClass);
      if (type != null) {
        return type;
      } else {
        if (nodeClass.isEnum()) {
          // Special case
          return new EnumType(nodeClass);
        }
        if (ICode.class.isAssignableFrom(nodeClass)) {
          // Special case
          return new CodeType(nodeClass);
        }
        return null;
      }
    }
  }


  /**
   * Does this node class identify an item node. That is, is this node class an
   * atomic item.
   */
  public boolean isItemType(Class<?> nodeClass) {
    return getType(nodeClass) != null;
  }


  public IType<?> getTypeByName(String name) {
    synchronized (namedTypes) {
      return namedTypes.get(name);
    }
  }
}
