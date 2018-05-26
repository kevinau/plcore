package org.plcore.userio.test;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.plcore.entity.EntityLife;
import org.plcore.entity.VersionTime;
import org.plcore.type.TypeRegistry;
import org.plcore.userio.Entity;
import org.plcore.userio.IOField;
import org.plcore.userio.UniqueConstraint;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IItemPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.IPlanFactory;
import org.plcore.userio.plan.impl.PlanFactory;


public class EntityKeyDataTest {

  @Entity
  @UniqueConstraint("code")
  public static class SimpleEntity {

    @IOField
    private int id;
    
    @IOField
    private VersionTime version;
    
    @IOField
    private String code;
    
    @IOField
    private String name;

    @IOField
    private String location;

    @IOField
    private EntityLife entityLife;
    
    @Override
    public String toString() {
      return code + " " + name + ", " + location;
    }

  }


  private IPlanFactory planFactory;

  @BeforeEach
  public void setup() {
    TypeRegistry typeRegistry = new TypeRegistry();
    typeRegistry.addZeroConfigurationBuiltins();
    
    planFactory = new PlanFactory(typeRegistry);
  }
  
  @Test
  public void testKeys () {
    IEntityPlan<SimpleEntity> plan = planFactory.getEntityPlan(SimpleEntity.class);
    
    IItemPlan<?>[] keyItems = plan.getKeyItems(0);
    Assertions.assertEquals(1, keyItems.length);
    Assertions.assertEquals("code", keyItems[0].getName());
  }

  
  @Test
  public void testData () {
    IEntityPlan<SimpleEntity> plan = planFactory.getEntityPlan(SimpleEntity.class);
    
    List<INodePlan> dataPlans = plan.getDataPlans();
    Assertions.assertEquals(3, dataPlans.size());
    Assertions.assertEquals("code", dataPlans.get(0).getName());
    Assertions.assertEquals("name", dataPlans.get(1).getName());
    Assertions.assertEquals("location", dataPlans.get(2).getName());
  }
}
