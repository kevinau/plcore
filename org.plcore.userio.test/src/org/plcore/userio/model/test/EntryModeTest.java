package org.plcore.userio.model.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.plcore.userio.EntryMode;
import org.plcore.userio.model.EffectiveEntryMode;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IItemPlan;
import org.plcore.userio.test.data.ModeTestEntity;
import org.plcore.userio.test.data.OuterModeTestEntity;


public class EntryModeTest extends ModelFactorySetup {

  @Test
  public void simplePlanAnnotations () {
    IEntityPlan<?> plan = planFactory.getEntityPlan(ModeTestEntity.class);
   
    IItemPlan<?> field0 = plan.selectItemPlan("field0");
    Assertions.assertEquals(EntryMode.UNSPECIFIED, field0.getEntryMode());

    IItemPlan<?> field1 = plan.selectItemPlan("field1");
    Assertions.assertEquals(EntryMode.ENABLED, field1.getEntryMode());
    
    IItemPlan<?> field2 = plan.selectItemPlan("field2");
    Assertions.assertEquals(EntryMode.DISABLED, field2.getEntryMode());
    
    IItemPlan<?> field3 = plan.selectItemPlan("field3");
    Assertions.assertEquals(EntryMode.VIEW, field3.getEntryMode());
    
    IItemPlan<?> field4 = plan.selectItemPlan("field4");
    Assertions.assertEquals(EntryMode.HIDDEN, field4.getEntryMode());
  }
  
  
  @Test
  public void simpleModeAnnotations () {
    IEntityModel model = modelFactory.buildEntityModel(ModeTestEntity.class);
    model.setValue(new ModeTestEntity());
    
    IItemModel field0 = model.selectItemModel("field0");
    Assertions.assertEquals(EffectiveEntryMode.ENABLED, field0.getEffectiveEntryMode());

    IItemModel field1 = model.selectItemModel("field1");
    Assertions.assertEquals(EffectiveEntryMode.ENABLED, field1.getEffectiveEntryMode());
    
    IItemModel field2 = model.selectItemModel("field2");
    Assertions.assertEquals(EffectiveEntryMode.DISABLED, field2.getEffectiveEntryMode());
    
    IItemModel field3 = model.selectItemModel("field3");
    Assertions.assertEquals(EffectiveEntryMode.VIEW, field3.getEffectiveEntryMode());
    
    IItemModel field4 = model.selectItemModel("field4");
    Assertions.assertEquals(EffectiveEntryMode.HIDDEN, field4.getEffectiveEntryMode());
  }
  
  
  @Test
  public void inheritedMode () {
    IEntityModel model = modelFactory.buildEntityModel(OuterModeTestEntity.class);
    model.setValue(new OuterModeTestEntity());
    
    INodeModel fieldx = model.selectNodeModel("inner");
    Assertions.assertEquals(EffectiveEntryMode.VIEW, fieldx.getEffectiveEntryMode());

    IItemModel field0 = model.selectItemModel("inner/field0");
    Assertions.assertEquals(EffectiveEntryMode.VIEW, field0.getEffectiveEntryMode());

    IItemModel field1 = model.selectItemModel("inner/field1");
    Assertions.assertEquals(EffectiveEntryMode.VIEW, field1.getEffectiveEntryMode());
    
    IItemModel field2 = model.selectItemModel("inner/field2");
    Assertions.assertEquals(EffectiveEntryMode.VIEW, field2.getEffectiveEntryMode());
    
    IItemModel field3 = model.selectItemModel("inner/field3");
    Assertions.assertEquals(EffectiveEntryMode.VIEW, field3.getEffectiveEntryMode());
    
    IItemModel field4 = model.selectItemModel("inner/field4");
    Assertions.assertEquals(EffectiveEntryMode.HIDDEN, field4.getEffectiveEntryMode());
    
    fieldx.setEntryMode(EntryMode.DISABLED);
    Assertions.assertEquals(EffectiveEntryMode.DISABLED, fieldx.getEffectiveEntryMode());
    Assertions.assertEquals(EffectiveEntryMode.DISABLED, field0.getEffectiveEntryMode());
    Assertions.assertEquals(EffectiveEntryMode.DISABLED, field1.getEffectiveEntryMode());
    Assertions.assertEquals(EffectiveEntryMode.DISABLED, field2.getEffectiveEntryMode());
    Assertions.assertEquals(EffectiveEntryMode.VIEW, field3.getEffectiveEntryMode());
    Assertions.assertEquals(EffectiveEntryMode.HIDDEN, field4.getEffectiveEntryMode());
  }
}
