package org.plcore.userio.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.plcore.type.TypeRegistry;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IItemPlan;
import org.plcore.userio.plan.impl.PlanFactory;
import org.plcore.userio.test.data.EntityWithSimpleArray;

public class EntityWithSimpleArrayTest {

  private PlanFactory planFactory = new PlanFactory();
  
  private IEntityPlan<?> plan;
  
  
  @BeforeEach
  public void before () {
    TypeRegistry typeRegistry = new TypeRegistry();
    typeRegistry.addZeroConfigurationBuiltins();
    
    planFactory = new PlanFactory(typeRegistry);
    plan = planFactory.getEntityPlan(EntityWithSimpleArray.class);
  }
  
  
//  @Test
//  public void testBasicPlan () {
//    INodePlan idPlan = plan.getIdPlan();
//    Assertions.assertNotNull(idPlan);
//    
//    List<INodePlan> dataPlans = plan.getDataPlans();
//    Assertions.assertEquals(2, dataPlans.size());
//  }
//  
//
//  @Test
//  public void testEntityLabels () {
//    EntityLabelGroup labels = plan.getLabels();
//    String title = labels.getTitle();
//    Assertions.assertEquals("Entity with simple array", title);
//    String description = labels.getDescription();
//    Assertions.assertEquals("", description);
//  }

  
  @Test
  public void testNodePaths () {
//    IItemPlan<?> field1Plan = plan.selectItemPlan("field1");
//    Assertions.assertEquals("field1", field1Plan.getName());
//    
//    INodePlan innerPlan = plan.selectNodePlan("field2");
//    Assertions.assertEquals("field2", innerPlan.getName());
//    Assertions.assertEquals(true, innerPlan instanceof IRepeatingPlan);
    
    IItemPlan<?> innerField1Plan = plan.selectItemPlan("field2/*");
    Assertions.assertEquals("field2", innerField1Plan.getName());
    
//    List<INodePlan> plans = plan.selectNodePlans("..");
//    Assertions.assertEquals(3, plans.size());
//    
//    List<IItemPlan<?>> plans2 = plan.selectItemPlans("..");
//    Assertions.assertEquals(2, plans2.size());
  }

}
