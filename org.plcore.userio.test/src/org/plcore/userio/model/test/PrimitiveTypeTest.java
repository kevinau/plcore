package org.plcore.userio.model.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.plcore.entity.EntityLife;
import org.plcore.entity.EntityLifeType;
import org.plcore.userio.Entity;
import org.plcore.userio.IOField;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.plan.IEntityPlan;

public class PrimitiveTypeTest extends ModelFactorySetup {

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
  
    
  @Test
  public void primitivesEntityModel () {
    IEntityPlan<Primitives> plan = planFactory.getEntityPlan(Primitives.class);
    Assertions.assertNotNull(plan, "Entity plan must not be null");

    IEntityModel model = modelFactory.buildEntityModel(plan);
    
    Primitives instance = new Primitives(true, 'A', (byte)123, (short)1234, 12345, 123456L, 12.34F, 1234.5678);
    model.setValue(instance);
    
    INodeModel[] items = model.getMembers();
    Assertions.assertEquals(8,  items.length);
    for (int i = 0; i < 8; i++) {
      Assertions.assertTrue(items[i] instanceof IItemModel);
      IItemModel itemModel = (IItemModel)items[i];
      Assertions.assertEquals(true, itemModel.getType().isPrimitive());
      Assertions.assertEquals(false, itemModel.getType().isNullable());
    }
    Object[] values = new Object[8];
    for (int i = 0; i < 8; i++) {
      values[i] = items[i].getValue();
    }
    Assertions.assertEquals(true, values[0]);
    Assertions.assertEquals('A', values[1]);
    Assertions.assertEquals((byte)123, values[2]);
    Assertions.assertEquals((short)1234, values[3]);
    Assertions.assertEquals(12345, values[4]);
    Assertions.assertEquals(123456L, values[5]);
    Assertions.assertEquals(12.34F, values[6]);
    Assertions.assertEquals(1234.5678, values[7]);
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
    Assertions.assertNotNull(plan, "Entity plan must not be null");

    ModelFactory modelFactory = new ModelFactory();
    IEntityModel model = modelFactory.buildEntityModel(plan);
    
    EntityLifeEntity instance = new EntityLifeEntity(EntityLife.ACTIVE);
    model.setValue(instance);
    
    INodeModel[] items = model.getMembers();
    Assertions.assertEquals(1,  items.length);
    for (int i = 0; i < items.length; i++) {
      Assertions.assertTrue(items[i] instanceof IItemModel);
      IItemModel itemModel = (IItemModel)items[i];
      Assertions.assertEquals(false, itemModel.getType().isPrimitive());
      Assertions.assertEquals(false, itemModel.getType().isNullable());
      Assertions.assertEquals(true, itemModel.getType() instanceof EntityLifeType);
    }
    Object[] values = new Object[1];
    for (int i = 0; i < values.length; i++) {
      IItemModel itemModel = (IItemModel)items[i];
      values[i] = itemModel.getValue();
    }
    Assertions.assertEquals(EntityLife.ACTIVE, values[0]);
  }

  
}
