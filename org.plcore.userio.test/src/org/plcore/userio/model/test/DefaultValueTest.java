package org.plcore.userio.model.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.plcore.userio.DefaultFor;
import org.plcore.userio.Embeddable;
import org.plcore.userio.IOField;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.INodeModel;

public class DefaultValueTest extends ModelFactorySetup {

  public static class Test1 {
    private Integer field1;
    
    private Integer field2 = 123;
    
  }
  
  
  @Test
  public void testNewSimpleClass () {
    IEntityModel model = modelFactory.buildEntityModel(Test1.class);
    
    Test1 instance = model.setNew();
    Assertions.assertNull(instance.field1);
    Assertions.assertEquals((Integer)123, instance.field2);

    INodeModel[] members = model.getMembers();
    Assertions.assertEquals(2, members.length);
    Assertions.assertEquals(null, ((IItemModel)members[0]).getValue());
    Assertions.assertEquals(null, ((IItemModel)members[0]).getDefaultValue());
    Assertions.assertEquals((Integer)123, ((IItemModel)members[1]).getValue());
    Assertions.assertEquals((Integer)123, ((IItemModel)members[1]).getDefaultValue());
  }
  
  
  @Embeddable
  public static class Inner {
    @SuppressWarnings("unused")
    private Integer inner0;
    
    public Inner () {
      this (123);
    }
    
    public Inner (int value) {
      this.inner0 = value;
    }
  }
  
  public static class Test2 {
    private Inner field1;
    
    private Inner field2 = new Inner(234);

  }
  
  
  @Test
  public void testArrayClass () {
    IEntityModel model = modelFactory.buildEntityModel(Test2.class);
    
    Test2 instance = model.setNew();
    Assertions.assertNull(instance.field1);
    Assertions.assertNotNull(instance.field2);
    
    INodeModel[] members = model.getMembers();
    Assertions.assertEquals(2, members.length);
    
    IItemModel item2 = model.selectItemModel("field2/inner0");
    Assertions.assertNotNull(item2);
    Assertions.assertEquals((Integer)234, item2.getDefaultValue());
    Assertions.assertEquals((Integer)234, item2.getValue());
  }


  public static class Test3 {
    private int field1 = 123;
    
    @IOField(min = 0)
    private int field2 = 234;
    
    private int field3 = 345;
    
    private int field4 = 456;
    
    @DefaultFor({"field3", "field4"})
    private int defaultFor() {
      return field1 + field2;
    }

    @Override
    public String toString() {
      return "Test3 [field1=" + field1 + ", field2=" + field2 + ", field3=" + field3 + ", field4=" + field4 + "]";
    }
    
  }
  
  
  @Test
  public void testDefaultFor () {
    IEntityModel model = modelFactory.buildEntityModel(Test3.class);
    
    Test3 instance = model.setNew();
    Assertions.assertEquals(123, instance.field1);
    Assertions.assertEquals(234, instance.field2);
    Assertions.assertEquals(357, instance.field3);
    Assertions.assertEquals(357, instance.field4);

    IItemModel model1 = model.selectItemModel("field1");
    IItemModel model2 = model.selectItemModel("field2");
    IItemModel model3 = model.selectItemModel("field3");
    IItemModel model4 = model.selectItemModel("field4");
    
    Assertions.assertEquals(123, (int)model1.getDefaultValue());
    Assertions.assertEquals(234, (int)model2.getDefaultValue());
    Assertions.assertEquals(357, (int)model3.getDefaultValue());
    Assertions.assertEquals(357, (int)model4.getDefaultValue());
    
    Assertions.assertEquals(123, (int)model1.getValue());
    model1.setDefaultValue(500);
    Assertions.assertEquals(500, (int)model1.getDefaultValue());
    Assertions.assertEquals(500, (int)model1.getValue());
    Assertions.assertEquals(734, (int)model3.getDefaultValue());
    Assertions.assertEquals(734, (int)model3.getValue());
    
    model1.setValue(1000);
    Assertions.assertEquals(500, (int)model1.getDefaultValue());
    Assertions.assertEquals(1000, (int)model1.getValue());
    Assertions.assertEquals(1234, (int)model3.getDefaultValue());
    Assertions.assertEquals(1234, (int)model3.getValue());
    
    model2.setValue(-100);
    // No change in values because of the error condition
    Assertions.assertEquals(500, (int)model1.getDefaultValue());
    Assertions.assertEquals(1000, (int)model1.getValue());
    Assertions.assertEquals(1234, (int)model3.getDefaultValue());
    Assertions.assertEquals(1234, (int)model3.getValue());

    model2.setValue(2000);
    Assertions.assertEquals(234, (int)model2.getDefaultValue());
    Assertions.assertEquals(2000, (int)model2.getValue());
    Assertions.assertEquals(3000, (int)model3.getDefaultValue());
    Assertions.assertEquals(3000, (int)model3.getValue());
    
  }
  
}
