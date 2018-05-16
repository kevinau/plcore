package org.plcore.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.plcore.type.builtin.BigDecimalType;
import org.plcore.type.builtin.BigIntegerType;
import org.plcore.type.builtin.BooleanType;
import org.plcore.type.builtin.ByteType;
import org.plcore.type.builtin.CharacterType;
import org.plcore.type.builtin.CodeType;
import org.plcore.type.builtin.DateType;
import org.plcore.type.builtin.DecimalType;
import org.plcore.type.builtin.DoubleType;
import org.plcore.type.builtin.EnumType;
import org.plcore.type.builtin.IntegerType;
import org.plcore.type.builtin.LocalDateType;
import org.plcore.type.builtin.LongType;
import org.plcore.type.builtin.PercentType;
import org.plcore.type.builtin.PrimitiveBooleanType;
import org.plcore.type.builtin.PrimitiveByteType;
import org.plcore.type.builtin.PrimitiveCharType;
import org.plcore.type.builtin.PrimitiveDoubleType;
import org.plcore.type.builtin.PrimitiveFloatType;
import org.plcore.type.builtin.PrimitiveIntType;
import org.plcore.type.builtin.PrimitiveLongType;
import org.plcore.type.builtin.PrimitiveShortType;
import org.plcore.type.builtin.ShortType;
import org.plcore.type.builtin.SqlDateType;
import org.plcore.type.builtin.StringType;
import org.plcore.value.ICode;


@Component(service = TypeRegistry.class, immediate = true)
public class TypeRegistry {

  private class DeclaredType {
    private final Pattern pattern;
    
    private final Class<?> fieldClass;

    private final IType<?> type;
    
    private DeclaredType (String nameRegex, Class<?> fieldClass, IType<?> type) {
      if (nameRegex == null) {
        this.pattern = null;
      } else {
        this.pattern = Pattern.compile(nameRegex);
      }
      if (fieldClass == null) {
        throw new IllegalArgumentException("fieldClass cannot be null");
      }
      this.fieldClass = fieldClass;
      this.type = type;
    }
    
    private boolean equals (String nameRegex, Class<?> fieldClass) {
      if (pattern != null) {
        if (!pattern.pattern().equals(nameRegex)) {
          return false;
        }
      }
      if (this.fieldClass != null) {
        return this.fieldClass.equals(fieldClass);
      } else {
        return true;
      }
    }
    
    private boolean matches (Class<?> fieldClass, String fieldName) {
      if (pattern != null) {
        Matcher matcher = pattern.matcher(fieldName);
        if (!matcher.matches()) {
          return false;
        }
      }
      if (this.fieldClass != null) {
        return this.fieldClass.isAssignableFrom(fieldClass);
      } else {
        return true;
      }
    }
  }
  
  private Map<String, IType<?>> namedTypes = new HashMap<>();
  private List<DeclaredType> declaredTypes = new ArrayList<>();


  @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  public void addType(IType<?> type, Map<String, Object> props) {
    synchronized (namedTypes) {
      synchronized (declaredTypes) {
        String name = (String)props.get("name");
        if (name != null) {
          IType<?> oldType = namedTypes.putIfAbsent(name, type);
          if (oldType != null) {
            throw new RuntimeException("Duplicate IType with the same name: " + name);
          }
        }
        
        String nameRegex = (String)props.get("matching");
        Class<?> fieldClass = type.getFieldClass();
        DeclaredType declaredType = new DeclaredType(nameRegex, fieldClass, type);
        declaredTypes.add(declaredType);
      }
    }
  }


  public synchronized void removeType(IType<?> type, Map<String, Object> props) {
    synchronized (namedTypes) {
      synchronized (declaredTypes) {
        String name = (String)props.get("name");
        if (name != null) {
          namedTypes.remove(name);
        }

        String nameRegex = (String)props.get("matching");
        Class<?> fieldClass = type.getFieldClass();
        int i = 0;
        for (DeclaredType declaredType : declaredTypes) {
          if (declaredType.equals(nameRegex, fieldClass)) {
            declaredTypes.remove(i);
            break;
          }
          i++;
        }
      }
    }
  }

  
  /** 
   * For testing, add all non-configured builtins.  For normal operation,
   * these types are added as components within OSGi.
   */
  public void addZeroConfigurationBuiltins () {
    Map<String, Object> props = new HashMap<>();
    addType (new PrimitiveBooleanType(), props);
    addType (new PrimitiveByteType(), props);
    addType (new PrimitiveCharType(), props);
    addType (new PrimitiveDoubleType(), props);
    addType (new PrimitiveFloatType(), props);
    addType (new PrimitiveIntType(), props);
    addType (new PrimitiveLongType(), props);
    addType (new PrimitiveShortType(), props);

    addType (new BigDecimalType(), props);
    addType (new BigIntegerType(), props);
    addType (new BooleanType(), props);
    addType (new ByteType(), props);
    addType (new CharacterType(), props);
    addType (new DateType(), props);
    addType (new DecimalType(), props);
    addType (new DoubleType(), props);
    addType (new IntegerType(), props);
    addType (new LocalDateType(), props);
    addType (new LongType(), props);
    addType (new PercentType(), props);
    addType (new ShortType(), props);
    addType (new SqlDateType(), props);
    addType (new StringType(), props);
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
  public IType<?> getByFieldClass(Class<?> fieldClass, String fieldName) {
    synchronized (declaredTypes) {
      for (DeclaredType declaredType : declaredTypes) {
        if (declaredType.matches(fieldClass, fieldName)) {
          return declaredType.type;
        }
      }
      if (fieldClass.isEnum()) {
        // Special case
        return new EnumType(fieldClass);
      }
      if (ICode.class.isAssignableFrom(fieldClass)) {
        // Special case
        return new CodeType(fieldClass);
      }
      return null;
    }
  }


  @SuppressWarnings("unchecked")
  public <X> IType<X> getByName(String name) {
    synchronized (namedTypes) {
      return (IType<X>)namedTypes.get(name);
    }
  }
}
