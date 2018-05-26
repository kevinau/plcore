package org.plcore.userio.model.test;

import java.math.BigDecimal;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.plcore.math.Decimal;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.test.data.DecimalBased;

public class DecimalBasedModels extends ModelFactorySetup {

  private IEntityModel model;
  private DecimalBased instance;
  

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
  protected void setup () {
    super.setup();
    model = modelFactory.buildEntityModel(DecimalBased.class);
    instance = new DecimalBased();
    instance.float1 = 123.45F;
    instance.double1 = 1234.56;
    instance.decimal = new Decimal("1234.56");
    instance.bigDecimal = new BigDecimal(1234.56);
    model.setValue(instance);
  }
  
  
  @Test
  public void floatTest () {
    getSetTest("float1", (Float)123.45F, "123.45", () -> instance.float1);
  }
  
  @Test
  public void doubleTest () {
    getSetTest("double1", (Double)1234.56, "1234.56", () -> instance.double1);
  }
  
  @Test
  public void bigDecimalTest () {
    getSetTest("bigDecimal", new BigDecimal(1234.56), "1234.56", () -> instance.bigDecimal);
  }
  
  @Test
  public void decimalTest () {
    getSetTest("decimal", new Decimal(1234.56), "1234.56", () -> instance.decimal);
  }
  
}
