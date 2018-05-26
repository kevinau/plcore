package org.plcore.userio.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.plcore.entity.EntityLife;
import org.plcore.entity.VersionTime;
import org.plcore.type.TypeRegistry;
import org.plcore.userio.Entity;
import org.plcore.userio.Id;
import org.plcore.userio.Version;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IItemPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.impl.PlanFactory;


public class SpecialEntityFieldsByAnnotation {

  @Entity
  public static class SimpleEntity {

    @Id
    private int identity;
    
    @Version 
    private VersionTime versionField;
    
    private String name;

    private String location;

    @SuppressWarnings("unused")
    private EntityLife entityLife;
    
    @Override
    public String toString() {
      return name + ", " + location;
    }

  }


  private PlanFactory planFactory;
  
  
  @BeforeEach
  public void before () {
    TypeRegistry typeRegistry = new TypeRegistry();
    typeRegistry.addZeroConfigurationBuiltins();
    
    planFactory = new PlanFactory(typeRegistry);
  }
  
  
  @Test
  public void testBasicPlan () {
    IEntityPlan<SimpleEntity> plan = planFactory.getEntityPlan(SimpleEntity.class);
    
    INodePlan idPlan = plan.getIdPlan();
    Assertions.assertNotNull(idPlan);
    Assertions.assertTrue(idPlan instanceof IItemPlan);
    Assertions.assertEquals("identity", idPlan.getName());
    
    INodePlan versionPlan = plan.getVersionPlan();
    Assertions.assertNotNull(versionPlan);
    Assertions.assertTrue(versionPlan instanceof IItemPlan);
    Assertions.assertEquals("versionField", versionPlan.getName());
    
    INodePlan entityLifePlan = plan.getEntityLifePlan();
    Assertions.assertNotNull(entityLifePlan);
    Assertions.assertTrue(entityLifePlan instanceof IItemPlan);
    Assertions.assertEquals("entityLife", entityLifePlan.getName());
  }

}
