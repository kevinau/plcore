package org.plcore.type.test;

import org.osgi.service.component.annotations.Component;
import org.plcore.test.Assert;
import org.plcore.test.ITestCase;
import org.plcore.test.Test;
import org.plcore.type.UserEntryException;
import org.plcore.type.builtin.LongType;

@Component
public class LongTypeTests implements ITestCase {

  private LongType longType = new LongType();
  
  @Test
  public void createFromStringTest () throws UserEntryException {
    long value = longType.createFromString("12345");
    Assert.assertEquals((long)12345L, value);
  }

  @Test
  public void LongFieldSizeTest () throws UserEntryException {
    int size = longType.getFieldSize();
    Assert.assertEquals(19, size);
  }

}
