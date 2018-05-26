package org.plcore.userio.model.test;

import java.math.BigInteger;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.test.data.IntegerBased;

public class IntegerBasedModels extends ModelFactorySetup {

  private IEntityModel model;
  private IntegerBased instance;
  

  private void getSetTest (String fieldName, Object value, String okSource, Supplier<?> supplier) {
    IItemModel itemModel = model.selectItemModel(fieldName);
    itemModel.setValue(value);
    Assertions.assertEquals(value, supplier.get(), "Field " + fieldName);
    Assertions.assertEquals(value, itemModel.getValue(), "Field " + fieldName);
    
    itemModel.setValueFromSource(okSource);
    Assertions.assertEquals(value, supplier.get(), "Field " + fieldName);
    Assertions.assertEquals(value, itemModel.getValue(), "Field " + fieldName);
  }
  
  
  @Override
  @BeforeEach
  public void setup () {
    super.setup();
    model = modelFactory.buildEntityModel(IntegerBased.class);
    instance = new IntegerBased();
    model.setValue(instance);
  }
  
  
  @Test
  public void byteTest () {
    getSetTest("byte1", (byte)123, "123", () -> instance.byte1);
  }
  
  @Test
  public void shortTest () {
    getSetTest("short1", (short)1234, "1234", () -> instance.short1);
  }
  
  @Test
  public void integerTest () {
    getSetTest("integer1", (int)12345, "12345", () -> instance.integer1);
  }
  
  @Test
  public void longTest () {
    getSetTest("long1", (long)123456, "123456", () -> instance.long1);
  }
  
  @Test
  public void bigIntegerTest () {
    getSetTest("bigInteger", BigInteger.valueOf(12345L), "12345", () -> instance.bigInteger);
  }
  
}
