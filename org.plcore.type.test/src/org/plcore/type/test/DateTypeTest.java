package org.plcore.type.test;

import java.time.LocalDate;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.plcore.type.IType;
import org.plcore.type.UserEntryException.Type;
import org.plcore.type.builtin.LocalDateType;


class DateTypeTest extends AbstractTypeTest {

  public static Arguments[] good() {
     return new Arguments[] {
       Arguments.of("120580", LocalDate.of(1980, 5, 12)),
       Arguments.of("12/05/80", LocalDate.of(1980, 5, 12)),
       Arguments.of("12May80", LocalDate.of(1980, 5, 12)),
       Arguments.of("12-MAY-80", LocalDate.of(1980, 5, 12)),
       Arguments.of("12 MAY 80", LocalDate.of(1980, 5, 12)),
       Arguments.of("12th May 80", LocalDate.of(1980, 5, 12)),
       Arguments.of("MAY12, 80", LocalDate.of(1980, 5, 12)),
       Arguments.of("MAY 12th, 80", LocalDate.of(1980, 5, 12)),
       Arguments.of("120512", LocalDate.of(2012, 5, 12)),
       Arguments.of("12 May80", LocalDate.of(1980, 5, 12)),
       Arguments.of("2080-05-12", LocalDate.of(2080, 5, 12)),
     };
  }
  
  
  public static Arguments[] good2() {
     return new Arguments[] {
       Arguments.of("12th May 1980", LocalDate.of(1980, 5, 12)),
       Arguments.of("MAY12, 1980", LocalDate.of(1980, 5, 12)),
       Arguments.of("12052080", LocalDate.of(2080, 5, 12)),
     };
  }
  
  
  public static Arguments[] incomplete() {
    Arguments[] good = good();
    
    // Count incomplete tests
    int n = 0;
    for (int i = 0; i < good.length; i++) {
      String source = (String)good[i].get()[0];
      n += source.length() - 2;
    }
    
    Arguments[] incomplete = new Arguments[n];
    int j = 0;
    for (int i = 0; i < good.length; i++) {
      String source = (String)good[i].get()[0];
      for (int x = 1; x < source.length() - 1; x++) {
        incomplete[j] = Arguments.of(source.substring(0, x), Type.INCOMPLETE, "invalid date");
        j++;
      }
    }
    return incomplete; 
  }
  
  
  public static Arguments[] bad() {
     return new Arguments[] {
       Arguments.of("", Type.REQUIRED, null),
       Arguments.of("1", Type.INCOMPLETE, "invalid date"),
       Arguments.of("12", Type.INCOMPLETE, "invalid date"),
       Arguments.of("00", Type.ERROR, "invalid date"),
       Arguments.of("32", Type.ERROR, "invalid date"),
       Arguments.of("120080", Type.ERROR, "invalid date"),
       Arguments.of("121380", Type.ERROR, "invalid date"),
       Arguments.of("1205208", Type.INCOMPLETE, "invalid date"),
       Arguments.of("12-05-2199898", Type.ERROR, "invalid date"),
       Arguments.of("12-05-201X", Type.ERROR, "invalid date"),
       Arguments.of("012-05-2018", Type.ERROR, "invalid date"),
       Arguments.of("12-005-2018", Type.ERROR, "invalid date"),
       Arguments.of("30 Feb 2017", Type.ERROR, "invalid date"),
       Arguments.of("28-Feb/2017", Type.ERROR, "invalid date"),
       Arguments.of("28 Feb/2017", Type.ERROR, "invalid date"),
       Arguments.of("30-Feb-2017", Type.ERROR, "invalid date"),
       Arguments.of("29 Feb 2017", Type.ERROR, "invalid date"),
       Arguments.of("12#", Type.ERROR, "invalid date"),
       Arguments.of("#", Type.ERROR, "invalid date"),
       Arguments.of("3104", Type.ERROR, "invalid date"),
       Arguments.of("3002", Type.ERROR, "invalid date"),
       Arguments.of("29022001", Type.ERROR, "invalid date"),
       Arguments.of("120520809", Type.ERROR, "invalid date"),
       Arguments.of("2012XXXX", Type.ERROR, "invalid date"),
       Arguments.of("2012-05-XX", Type.ERROR, "invalid date"),
       Arguments.of("2012-05=12", Type.ERROR, "invalid date"),
       Arguments.of("2012-05-99", Type.ERROR, "invalid date"),
       Arguments.of("05-Ma-99", Type.ERROR, "invalid date"),
     };
  }
  
  
  @ParameterizedTest
  @MethodSource("good")
  public void testGoodDates(String source, Object result) {
    IType<?> type = new LocalDateType();
    super.testGoodSource(type, source, result);
  }

  
  @ParameterizedTest
  @MethodSource("good2")
  public void testGoodYYYYDates(String source, Object result) {
    IType<?> type = new LocalDateType();
    super.testGoodSource(type, source, result);
  }

  
  @ParameterizedTest
  @MethodSource("incomplete")
  public void testIncompleteDates(String source, Type exType, String exMessage) {
    IType<?> type = new LocalDateType();
    super.testBadSource(type, source, exType, exMessage);
  }
  

  @ParameterizedTest
  @MethodSource("bad")
  public void testBadDates(String source, Type exType, String exMessage) {
    IType<?> type = new LocalDateType();
    super.testBadSource(type, source, exType, exMessage);
  }
  
}
