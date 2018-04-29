package org.plcore.type.test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.plcore.type.IType;
import org.plcore.type.UserEntryException.Type;
import org.plcore.type.builtin.BooleanType;

public class BooleanTypeTest extends AbstractTypeTest {

  public static Arguments[] good() {
     return new Arguments[] {
       Arguments.of("Yes", true),
       Arguments.of("y", true),
       Arguments.of("1", true),
       Arguments.of("N", false),
       Arguments.of("no", false),
       Arguments.of("0", false),
     };
  }
  
  
  public static Arguments[] bad() {
     return new Arguments[] {
       Arguments.of("X", Type.ERROR, "not a yes/no value"),
     };
  }
  
  
  @ParameterizedTest
  @MethodSource("good")
  public void testGoodSource(String source, Object result) {
    IType<?> type = new BooleanType();
    super.testGoodSource(type, source, result);
  }

  
  @ParameterizedTest
  @MethodSource("bad")
  public void testBadSource(String source, Type exType, String exMessage) {
    IType<?> type = new BooleanType();
    super.testBadSource(type, source, exType, exMessage);
  }
  
}
