package org.plcore.userio.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.plcore.entity.EntityLife;
import org.plcore.entity.VersionTime;
import org.plcore.type.TypeRegistry;
import org.plcore.userio.Entity;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IItemPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.impl.PlanFactory;


public class SpecialEntityFieldsByConvention {

  @Entity
  @SuppressWarnings("unused")
  public static class SimpleEntity {

    private int id;
    
    private VersionTime version;
    
    private String name;

    private String location;

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
  public void entityPlanByConvention () {
    IEntityPlan<SimpleEntity> plan = planFactory.getEntityPlan(SimpleEntity.class);
    
    INodePlan idPlan = plan.getIdPlan();
    Assertions.assertNotNull(idPlan);
    Assertions.assertTrue(idPlan instanceof IItemPlan);
    Assertions.assertEquals("id", idPlan.getName());
    
    INodePlan versionPlan = plan.getVersionPlan();
    Assertions.assertNotNull(versionPlan);
    Assertions.assertTrue(versionPlan instanceof IItemPlan);
    Assertions.assertEquals("version", versionPlan.getName());
    
    INodePlan entityLifePlan = plan.getEntityLifePlan();
    Assertions.assertNotNull(entityLifePlan);
    Assertions.assertTrue(entityLifePlan instanceof IItemPlan);
    Assertions.assertEquals("entityLife", entityLifePlan.getName());
  }

}
