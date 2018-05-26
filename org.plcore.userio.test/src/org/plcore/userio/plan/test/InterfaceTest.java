package org.plcore.userio.plan.test;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.plcore.userio.IOField;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IInterfacePlan;
import org.plcore.userio.plan.INodePlan;


public class InterfaceTest extends PlanFactorySetup {

  public static interface Inner {
    @IOField
    public String getName();
    
    public void setName(String name);
  }
  
  public static class Outer {
    @SuppressWarnings("unused")
    private Inner inner1;
  }
  
  @Test
  public void planFactoryTest () {
    Assert.assertNotNull(planFactory);
    IEntityPlan<Outer> plan = planFactory.getEntityPlan(Outer.class);
    INodePlan[] members = plan.getMembers();
    Assert.assertEquals(1, members.length);
    Assert.assertTrue(members[0] instanceof IInterfacePlan);
  }
  
}
