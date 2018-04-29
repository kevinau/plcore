package org.plcore.type.test;

import org.junit.Assert;
import org.plcore.type.IType;
import org.plcore.type.UserEntryException;
import org.plcore.type.UserEntryException.Type;


public class AbstractTypeTest {

  public void testGoodSource(IType<?> type, String source, Object result) {
    try {
      Object value = type.createFromString(source);
      Assert.assertEquals(value, result);
    } catch (UserEntryException ex) {
      Assert.fail(ex.toString());
    }
  }
  
  
  public void testBadSource(IType<?> type, String source, Type exType, String exMessage) {
    try {
      type.createFromString(source);
      Assert.fail("Expecting UserEntryException");
    } catch (UserEntryException ex) {
      Assert.assertEquals(ex.getType(), exType);
      Assert.assertEquals(ex.getMessage(), exMessage);
    }
  }
}
