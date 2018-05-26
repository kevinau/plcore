package org.plcore.userio.plan.test;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.plcore.userio.IOField;
import org.plcore.userio.Computed;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.INodePlan;


public class ExcludedFieldsTest extends PlanFactorySetup {

  @SuppressWarnings("unused")
  public static class X {
    @IOField
    private Integer field1;
    
    @Computed
    private Integer nonField;
    
    private static Integer staticField;
    
    private final Integer finalField = 123;
    
    private volatile Integer volatileField;
    
  }
  
  @Test
  public void excludedFieldsTest () {
    Assert.assertNotNull(planFactory);
    IEntityPlan<X> plan = planFactory.getEntityPlan(X.class);
    INodePlan[] members = plan.getMembers();
    Assert.assertEquals(1, members.length);
  }
  
}
