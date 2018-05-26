package org.plcore.userio.model.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.plcore.type.UserEntryException;
import org.plcore.userio.Entity;
import org.plcore.userio.IOField;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.model.ItemEventListener;

public class ItemEventsTest extends ModelFactorySetup {
  
  @Entity
  public static class StandardEntity {

    @IOField
    private int field1;
    
    public StandardEntity() {
      this.field1 = 123;
    }
    

    public StandardEntity(int field1) {
      this.field1 = field1;
    }

    public int getField1() {
      return field1;
    }
    
    public void setField1(int field1) {
      this.field1 = field1;
    }
    
  }

    
  private class EventCounter implements ItemEventListener {

    private int valueChangeCount = 0;
    //private int valueEqualityChangeCount = 0;
    private int sourceChangeCount = 0;
    //private int sourceEqualityChangeCount = 0;
    private int errorNoted = 0;
    private int errorCleared = 0;
    //private UserEntryException lastError;
    
    
    @Override
    public String getOrigin() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public void valueEqualityChange(INodeModel node, boolean isEqual) {
      //valueEqualityChangeCount++;
    }

    @Override
    public void sourceEqualityChange(INodeModel node, boolean isEqual) {
      //sourceEqualityChangeCount++;
    }

    @Override
    public void valueChange(INodeModel node) {
      valueChangeCount++;
    }

    @Override
    public void errorCleared(INodeModel node) {
      errorCleared++;
    }

    @Override
    public void errorNoted(INodeModel node, UserEntryException ex) {
      errorNoted++;
      //lastError = ex;
    }

    @Override
    public void sourceChange(INodeModel node) {
      sourceChangeCount++;
    }

    @Override
    public void comparisonBasisChange(INodeModel node) {
      // TODO Auto-generated method stub
    }      
    
  }
  
  
  @Test
  public void testItemEvents () {
    IEntityModel model = modelFactory.buildEntityModel(StandardEntity.class);
    
    StandardEntity instance = new StandardEntity();
    model.setValue(instance);
    
    IItemModel itemModel = model.getMember("field1");  
    Assertions.assertNotNull(itemModel);

    EventCounter eventCounter = new EventCounter();
    itemModel.addItemEventListener(eventCounter);

    itemModel.setValue(234);
    Assertions.assertEquals(1, eventCounter.valueChangeCount);
    Assertions.assertEquals(1, eventCounter.sourceChangeCount);

    itemModel.setValueFromSource("345");
    Assertions.assertEquals(2, eventCounter.valueChangeCount);
    Assertions.assertEquals(2, eventCounter.sourceChangeCount);

    itemModel.setValueFromSource("0345");
    Assertions.assertEquals(2, eventCounter.valueChangeCount);
    Assertions.assertEquals(3, eventCounter.sourceChangeCount);
    Assertions.assertEquals(0, eventCounter.errorNoted);
  }  

  @Test
  public void testItemEventsViaEntity () {
    IEntityModel model = modelFactory.buildEntityModel(StandardEntity.class);
    
    EventCounter eventCounter = new EventCounter();
    model.addItemEventListener(eventCounter);
    Assertions.assertEquals(0, eventCounter.valueChangeCount);
    Assertions.assertEquals(0, eventCounter.sourceChangeCount);

    StandardEntity instance = new StandardEntity();
    model.setValue(instance);
    
    Assertions.assertEquals(1, eventCounter.valueChangeCount);
    Assertions.assertEquals(1, eventCounter.sourceChangeCount);

    StandardEntity instance2 = new StandardEntity(345);
    model.setValue(instance2);

    Assertions.assertEquals(2, eventCounter.valueChangeCount);
    Assertions.assertEquals(2, eventCounter.sourceChangeCount);
  }  

  @Test
  public void testItemErrors () {
    IEntityModel model = modelFactory.buildEntityModel(StandardEntity.class);
    
    StandardEntity instance = new StandardEntity();
    model.setValue(instance);
    
    IItemModel itemModel = model.getMember("field1");  
    Assertions.assertNotNull(itemModel);

    EventCounter eventCounter = new EventCounter();
    itemModel.addItemEventListener(eventCounter);

    itemModel.setValueFromSource("ABCD");
    Assertions.assertEquals(1, eventCounter.errorNoted);

    itemModel.setValueFromSource("1234");
    Assertions.assertEquals(1, eventCounter.errorNoted);
    Assertions.assertEquals(1, eventCounter.errorCleared);
  }  

}
