package org.plcore.userio.model.test;

import java.text.ParseException;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.test.Assert;
import org.plcore.test.Before;
import org.plcore.test.ITestCase;
import org.plcore.test.Test;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.IModelFactory;
import org.plcore.userio.test.data.CodeBased;
import org.plcore.userio.test.data.CodeBased.Gender;
import org.plcore.userio.test.data.CodeBased.Weekday;
import org.plcore.value.EntityLife;


@Component
public class CodeBasedModels implements ITestCase {

  @Reference
  private IModelFactory modelFactory;
  
  private IEntityModel model;
  private CodeBased instance;
  

  private void getSetTest (String fieldName, Object value, String okSource, Supplier<?> supplier) {
    IItemModel itemModel = model.selectItemModel(fieldName);
    itemModel.setValue(value);
    Assert.assertEquals("Field " + fieldName, value, supplier.get());
    Assert.assertEquals("Field " + fieldName, value, itemModel.getValue());
    
    itemModel.setValueFromSource(okSource);
    Assert.assertEquals("Field " + fieldName, value, supplier.get());
    Assert.assertEquals("Field " + fieldName, value, itemModel.getValue());
  }
  
  
  @Before
  public void setup () {
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
