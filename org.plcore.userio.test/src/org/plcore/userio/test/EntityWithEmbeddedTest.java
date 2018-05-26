package org.plcore.userio.test;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.plcore.type.TypeRegistry;
import org.plcore.userio.plan.EntityLabelGroup;
import org.plcore.userio.plan.IEmbeddedPlan;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IItemPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.IPlanFactory;
import org.plcore.userio.plan.impl.PlanFactory;
import org.plcore.userio.test.data.EntityWithEmbedded;

public class EntityWithEmbeddedTest {

  private IPlanFactory planFactory;
  
  private IEntityPlan<?> plan;
  
  
  @BeforeEach
  public void before () {
    TypeRegistry typeRegistry = new TypeRegistry();
    typeRegistry.addZeroConfigurationBuiltins();
    
    planFactory = new PlanFactory(typeRegistry);
    plan = planFactory.getEntityPlan(EntityWithEmbedded.class);
  }
  
  
  @Test
  public void testBasicPlan () {
    INodePlan idPlan = plan.getIdPlan();
    Assertions.assertNotNull(idPlan);
    
    List<INodePlan> dataPlans = plan.getDataPlans();
    Assertions.assertEquals(2, dataPlans.size());
  }
  

  @Test
  public void testEntityLabels () {
    EntityLabelGroup labels = plan.getLabels();
    String title = labels.getTitle();
    Assertions.assertEquals("Entity with embedded", title);
    String description = labels.getDescription();
    Assertions.assertEquals("", description);
  }

  
  @Test
  public void testNodePaths () {
    IItemPlan<?> field1Plan = plan.selectItemPlan("field1");
    Assertions.assertEquals("field1", field1Plan.getName());
    
    INodePlan innerPlan = plan.selectNodePlan("inner");
    Assertions.assertEquals("inner", innerPlan.getName());
    Assertions.assertEquals(true, innerPlan instanceof IEmbeddedPlan);
    
    IItemPlan<?> innerField1Plan = plan.selectItemPlan("inner/field1");
    Assertions.assertEquals("field1", innerField1Plan.getName());
    
    IItemPlan<?> innerField2Plan = plan.selectItemPlan("inner/field2");
    Assertions.assertEquals("field2", innerField2Plan.getName());
    
    List<IItemPlan<?>> plans = plan.selectItemPlans("**");
    Assertions.assertEquals(4, plans.size());
  }

}
