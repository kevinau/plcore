package org.plcore.userio.model.test;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.test.Assert;
import org.plcore.test.ITestCase;
import org.plcore.test.Test;
import org.plcore.userio.EntryMode;
import org.plcore.userio.model.EffectiveEntryMode;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.IModelFactory;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IItemPlan;
import org.plcore.userio.plan.IPlanFactory;
import org.plcore.userio.test.data.ModeTestEntity;
import org.plcore.userio.test.data.OuterModeTestEntity;


@Component
public class EntryModeTest implements ITestCase {

  @Reference
  private IPlanFactory planFactory;
  
  @Reference
  private IModelFactory modelFactory;
  
  
  @Test
  public void simplePlanAnnotations () {
    IEntityPlan<?> plan = planFactory.getEntityPlan(ModeTestEntity.class);
   
    IItemPlan<?> field0 = plan.selectItemPlan("field0");
    Assert.assertEquals(EntryMode.UNSPECIFIED, field0.getEntryMode());

    IItemPlan<?> field1 = plan.selectItemPlan("field1");
    Assert.assertEquals(EntryMode.ENABLED, field1.getEntryMode());
    
    IItemPlan<?> field2 = plan.selectItemPlan("field2");
    Assert.assertEquals(EntryMode.DISABLED, field2.getEntryMode());
    
    IItemPlan<?> field3 = plan.selectItemPlan("field3");
    Assert.assertEquals(EntryMode.VIEW, field3.getEntryMode());
    
    IItemPlan<?> field4 = plan.selectItemPlan("field4");
    Assert.assertEquals(EntryMode.HIDDEN, field4.getEntryMode());
  }
  
  
  @Test
  public void simpleModeAnnotations () {
    IEntityModel model = modelFactory.buildEntityModel(ModeTestEntity.class);
    model.setValue(new ModeTestEntity());
    
    IItemModel field0 = model.selectItemModel("field0");
    Assert.assertEquals(EffectiveEntryMode.ENABLED, field0.getEffectiveEntryMode());

    IItemModel field1 = model.selectItemModel("field1");
    Assert.assertEquals(EffectiveEntryMode.ENABLED, field1.getEffectiveEntryMode());
    
    IItemModel field2 = model.selectItemModel("field2");
    Assert.assertEquals(EffectiveEntryMode.DISABLED, field2.getEffectiveEntryMode());
    
    IItemModel field3 = model.selectItemModel("field3");
    Assert.assertEquals(EffectiveEntryMode.VIEW, field3.getEffectiveEntryMode());
    
    IItemModel field4 = model.selectItemModel("field4");
    Assert.assertEquals(EffectiveEntryMode.HIDDEN, field4.getEffectiveEntryMode());
  }
  
  
  @Test
  public void inheritedMode () {
    IEntityModel model = modelFactory.buildEntityModel(OuterModeTestEntity.class);
    model.setValue(new OuterModeTestEntity());
    
    INodeModel fieldx = model.selectNodeModel("inner");
    Assert.assertEquals(EffectiveEntryMode.VIEW, fieldx.getEffectiveEntryMode());

    IItemModel field0 = model.selectItemModel("inner/field0");
    Assert.assertEquals(EffectiveEntryMode.VIEW, field0.getEffectiveEntryMode());

    IItemModel field1 = model.selectItemModel("inner/field1");
    Assert.assertEquals(EffectiveEntryMode.VIEW, field1.getEffectiveEntryMode());
    
    IItemModel field2 = model.selectItemModel("inner/field2");
    Assert.assertEquals(EffectiveEntryMode.VIEW, field2.getEffectiveEntryMode());
    
    IItemModel field3 = model.selectItemModel("inner/field3");
    Assert.assertEquals(EffectiveEntryMode.VIEW, field3.getEffectiveEntryMode());
    
    IItemModel field4 = model.selectItemModel("inner/field4");
    Assert.assertEquals(EffectiveEntryMode.HIDDEN, field4.getEffectiveEntryMode());
    
    fieldx.setEntryMode(EntryMode.DISABLED);
    Assert.assertEquals(EffectiveEntryMode.DISABLED, fieldx.getEffectiveEntryMode());
    Assert.assertEquals(EffectiveEntryMode.DISABLED, field0.getEffectiveEntryMode());
    Assert.assertEquals(EffectiveEntryMode.DISABLED, field1.getEffectiveEntryMode());
    Assert.assertEquals(EffectiveEntryMode.DISABLED, field2.getEffectiveEntryMode());
    Assert.assertEquals(EffectiveEntryMode.VIEW, field3.getEffectiveEntryMode());
    Assert.assertEquals(EffectiveEntryMode.HIDDEN, field4.getEffectiveEntryMode());
  }
}
