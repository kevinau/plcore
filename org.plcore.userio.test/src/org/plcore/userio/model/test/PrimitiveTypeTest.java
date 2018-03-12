package org.plcore.userio.model.test;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.test.Assert;
import org.plcore.test.ITestCase;
import org.plcore.test.Test;
import org.plcore.type.builtin.EntityLifeType;
import org.plcore.userio.Entity;
import org.plcore.userio.IOField;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IPlanFactory;
import org.plcore.value.EntityLife;

@Component
public class PrimitiveTypeTest implements ITestCase {

  @Entity
  public static class Primitives {
    @IOField
    private boolean field1;

    @IOField
    private char field2;

    @IOField
    private byte field3;

    @IOField
    private short field4;

    @IOField
    private int field5;

    @IOField
    private long field6;

    @IOField
    private float field7;

    @IOField
    private double field8;

    public Primitives(boolean field1, char field2, byte field3, short field4, int field5, long field6,
        float field7, double field8) {
      this.field1 = field1;
      this.field2 = field2;
      this.field3 = field3;
      this.field4 = field4;
      this.field5 = field5;
      this.field6 = field6;
      this.field7 = field7;
      this.field8 = field8;
    }

  }
  
  
  @Reference
  private IPlanFactory planFactory;
  
  
  @Test
  public void primitivesEntityModel () {
    IEntityPlan<Primitives> plan = planFactory.getEntityPlan(Primitives.class);
    Assert.assertNotNull("Entity plan must not be null", plan);

    ModelFactory modelFactory = new ModelFactory();
    IEntityModel model = modelFactory.buildEntityModel(plan);
    
    Primitives instance = new Primitives(true, 'A', (byte)123, (short)1234, 12345, 123456L, 12.34F, 1234.5678);
    model.setValue(instance);
    
    INodeModel[] items = model.getMembers();
    Assert.assertEquals(8,  items.length);
    for (int i = 0; i < 8; i++) {
      Assert.assertTrue(items[i] instanceof IItemModel);
      IItemModel itemModel = (IItemModel)items[i];
      Assert.assertEquals(true, itemModel.getType().isPrimitive());
      Assert.assertEquals(false, itemModel.getType().isNullable());
    }
    Object[] values = new Object[8];
    for (int i = 0; i < 8; i++) {
      values[i] = items[i].getValue();
    }
    Assert.assertEquals(true, values[0]);
    Assert.assertEquals('A', values[1]);
    Assert.assertEquals((byte)123, values[2]);
    Assert.assertEquals((short)1234, values[3]);
    Assert.assertEquals(12345, values[4]);
    Assert.assertEquals(123456L, values[5]);
    Assert.assertEquals(12.34F, values[6]);
    Assert.assertEquals(1234.5678, values[7]);
  }
  
  
  @Entity
  public static class EntityLifeEntity {
    @IOField
    private EntityLife field1;

    public EntityLifeEntity(EntityLife field1) {
      this.field1 = field1;
    }
   
    public EntityLife getField1() {
      return field1;
    }
    
    public void setField1(EntityLife field1) {
      this.field1 = field1;
    }
    
  }
  
  
  @Test
  public void entityLifeEntityModel () {
    IEntityPlan<EntityLifeEntity> plan = planFactory.getEntityPlan(EntityLifeEntity.class);
    Assert.assertNotNull("Entity plan must not be null", plan);

    ModelFactory modelFactory = new ModelFactory();
    IEntityModel model = modelFactory.buildEntityModel(plan);
    
    EntityLifeEntity instance = new EntityLifeEntity(EntityLife.ACTIVE);
    model.setValue(instance);
    
    INodeModel[] items = model.getMembers();
    Assert.assertEquals(1,  items.length);
    for (int i = 0; i < items.length; i++) {
      Assert.assertTrue(items[i] instanceof IItemModel);
      IItemModel itemModel = (IItemModel)items[i];
      Assert.assertEquals(false, itemModel.getType().isPrimitive());
      Assert.assertEquals(false, itemModel.getType().isNullable());
      Assert.assertEquals(true, itemModel.getType() instanceof EntityLifeType);
    }
    Object[] values = new Object[1];
    for (int i = 0; i < values.length; i++) {
      IItemModel itemModel = (IItemModel)items[i];
      values[i] = itemModel.getValue();
    }
    Assert.assertEquals(EntityLife.ACTIVE, values[0]);
  }

  
}
