package org.plcore.userio.model.test;

import java.text.ParseException;
import java.util.function.Supplier;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.plcore.entity.EntityLife;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.test.data.CodeBased;
import org.plcore.userio.test.data.CodeBased.Gender;
import org.plcore.userio.test.data.CodeBased.Weekday;


public class CodeBasedModels extends ModelFactorySetup {

  private IEntityModel model;
  private CodeBased instance;
  

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
    model = modelFactory.buildEntityModel(CodeBased.class);
    instance = new CodeBased();
    model.setValue(instance);
  }
  
  
  @Test
  public void booleanTest () throws ParseException {
    getSetTest("boolean1", Boolean.TRUE, "Y", () -> instance.boolean1);
  }
  
  
  @Test
  public void genderTest () throws ParseException {
    getSetTest("gender", Gender.FEMALE, "F", () -> instance.gender);
  }
  
  
  @Test
  public void entityLifeTest () throws ParseException {
    getSetTest("entityLife", EntityLife.RETIRED, "R", () -> instance.entityLife);
  }
  
  
  @Test
  public void codeTest () throws ParseException {
    getSetTest("weekday", Weekday.WED, "wed", () -> instance.weekday);
  }
  
}
