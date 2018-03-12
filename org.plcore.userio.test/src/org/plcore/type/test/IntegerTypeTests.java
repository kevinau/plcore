package org.plcore.type.test;

import org.osgi.service.component.annotations.Component;
import org.plcore.test.Assert;
import org.plcore.test.ITestCase;
import org.plcore.test.Test;
import org.plcore.type.UserEntryException;
import org.plcore.type.builtin.IntegerType;

@Component
public class IntegerTypeTests implements ITestCase {

  private IntegerType integerType = new IntegerType();
  
  @Test
  public void createFromStringTest () throws UserEntryException {
    Integer value = integerType.createFromString("12345");
    Assert.assertEquals((Integer)12345, value);
  }

  @Test
  public void integerFieldSizeTest () throws UserEntryException {
    Integer size = integerType.getFieldSize();
    Assert.assertEquals((Integer)11, size);
  }

}
