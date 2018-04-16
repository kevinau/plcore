package org.plcore.userio.plan;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.plcore.type.ICaseSettable;
import org.plcore.type.ILengthSettable;
import org.plcore.type.IMaxSettable;
import org.plcore.type.IMinSettable;
import org.plcore.type.IPatternSettable;
import org.plcore.type.IPrecisionSettable;
import org.plcore.type.IScaleSettable;
import org.plcore.type.ISignSettable;
import org.plcore.type.IType;
import org.plcore.type.NumberSign;
import org.plcore.type.TextCase;
import org.plcore.userio.IOField;


public class TypeResolver {

  private static IType<?> copy(IType<?> fieldType, IType<?> fieldTypeCopy) {
    // Identity comparison (==) is correct here
    if (fieldType == fieldTypeCopy) {
      // We don't yet have a mutable copy of the fieldType
      Class<?> klass = fieldType.getClass();
      try {
        // Try for a copy constructor
        Constructor<?> copyConstructor = klass.getDeclaredConstructor(klass);
        return (IType<?>)copyConstructor.newInstance(fieldType);
      } catch (NoSuchMethodException ex) {
        // Throw a specific exception
        throw new RuntimeException(klass.getSimpleName() + " is mutable.  It must implement a copy constructor");
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
        throw new RuntimeException(ex);
      }
    } else {
      // We don't create another copy
      return fieldTypeCopy;
    }
  }


  public static IType<?> resolve(IType<?> fieldType, IOField fieldAnn) {
    IType<?> copyType = fieldType;

    // ... and then set parameters from the ItemField annotation.
    if (fieldAnn != null) {
      if (fieldType instanceof ILengthSettable) {
        int n = fieldAnn.length();
        if (n != -1) {
          copyType = copy(fieldType, copyType);
          ((ILengthSettable)copyType).setMaxLength(n);
        }
      }

      // The order of the following is important for integers. The following
      // combinations are allowed:
      // - min and max
      // - sign and max
      // - sign and precision
      // - precision (assumed to be signed)
      // To get this to work, precision and max come first. After that, min and
      // sign.
      if (fieldType instanceof IPrecisionSettable) {
        int n = fieldAnn.precision();
        if (n != -1) {
          copyType = copy(fieldType, copyType);
          ((IPrecisionSettable)copyType).setPrecision(n);
        }
      }
      if (fieldType instanceof IMaxSettable) {
        long max = fieldAnn.max();
        if (max != Long.MAX_VALUE) {
          copyType = copy(fieldType, copyType);
          ((IMaxSettable)copyType).setMax(max);
        }
      }
      if (fieldType instanceof IMinSettable) {
        long min = fieldAnn.min();
        if (min != Long.MIN_VALUE) {
          copyType = copy(fieldType, copyType);
          ((IMinSettable)copyType).setMin(min);
        }
      }
      if (fieldType instanceof ISignSettable) {
        NumberSign ns = fieldAnn.sign();
        if (ns != NumberSign.UNSPECIFIED) {
          copyType = copy(fieldType, copyType);
          ((ISignSettable)copyType).setSign(ns);
        }
      }
      if (fieldType instanceof IScaleSettable) {
        int n = fieldAnn.scale();
        if (n != -1) {
          copyType = copy(fieldType, copyType);
          ((IScaleSettable)copyType).setScale(n);
        }
      }
      if (fieldType instanceof IPatternSettable) {
        String pattern = fieldAnn.pattern();
        if (pattern.length() > 0) {
          // If pattern is specified, an targetName can also be
          // specified
          String targetName = fieldAnn.targetName();
          if (targetName.length() == 0) {
            targetName = null;
          }
          copyType = copy(fieldType, copyType);
          ((IPatternSettable)copyType).setPattern(pattern, targetName);
        }
      }
      if (fieldType instanceof ICaseSettable) {
        TextCase xcase = fieldAnn.xcase();
        if (xcase != TextCase.UNSPECIFIED) {
          copyType = copy(fieldType, copyType);
          ((ICaseSettable)copyType).setAllowedCase(xcase);
        }
      }
    }
    return copyType;
  }

}
