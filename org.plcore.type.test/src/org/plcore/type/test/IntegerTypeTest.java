package org.plcore.type.test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.plcore.type.IType;
import org.plcore.type.UserEntryException.Type;
import org.plcore.type.builtin.IntegerType;

public class IntegerTypeTest extends AbstractTypeTest {

  public static Arguments[] goodSigned() {
     return new Arguments[] {
       Arguments.of("1234", 1234),
       Arguments.of("-1234", -1234),
       Arguments.of("+1234", 1234),
       Arguments.of("0012", 12),
       Arguments.of("0", 0),
     };
  }
  
  
  public static Arguments[] badSigned() {
     return new Arguments[] {
       Arguments.of("", Type.REQUIRED, "required"),
       Arguments.of("+", Type.INCOMPLETE, "not a signed number"),
       Arguments.of("-", Type.INCOMPLETE, "not a signed number"),
       Arguments.of("++", Type.ERROR, "not a signed number"),
       Arguments.of("abc", Type.ERROR, "not a signed number"),
       Arguments.of("1234567890123", Type.ERROR, "number not in the range -2147483648 to 2147483647"),
     };
  }
  
  
  public static Arguments[] goodUnsigned() {
     return new Arguments[] {
       Arguments.of("1234", 1234),
       Arguments.of("0012", 12),
       Arguments.of("0", 0),
     };
  }
  
  
  public static Arguments[] badUnsigned() {
     return new Arguments[] {
       Arguments.of("", Type.REQUIRED, "required"),
       Arguments.of("+", Type.ERROR, "no sign allowed"),
       Arguments.of("-", Type.ERROR, "no sign allowed"),
       Arguments.of("abc", Type.ERROR, "not a number"),
       Arguments.of("12345", Type.ERROR, "number not in the range 0 to 9999"),
     };
  }
  
  
  @ParameterizedTest
  @MethodSource("goodSigned")
  public void testGoodSigned(String source, Object result) {
    IType<?> type = new IntegerType();
    super.testGoodSource(type, source, result);
  }

  
  @ParameterizedTest
  @MethodSource("badSigned")
  public void testBadSigned(String source, Type exType, String exMessage) {
    IType<?> type = new IntegerType();
    super.testBadSource(type, source, exType, exMessage);
  }
  
  
  @ParameterizedTest
  @MethodSource("goodUnsigned")
  public void testGoodSource(String source, Object result) {
    IType<?> type = new IntegerType(0, 9999);
    super.testGoodSource(type, source, result);
  }

  
  @ParameterizedTest
  @MethodSource("badUnsigned")
  public void testBadSource(String source, Type exType, String exMessage) {
    IType<?> type = new IntegerType(0, 9999);
    super.testBadSource(type, source, exType, exMessage);
  }
  
}
