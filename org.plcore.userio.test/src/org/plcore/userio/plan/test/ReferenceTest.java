package org.plcore.userio.plan.test;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.plcore.userio.IOField;
import org.plcore.userio.ManyToOne;
import org.plcore.userio.OneToOne;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.IReferencePlan;


public class ReferenceTest extends PlanFactorySetup {

  public static class Inner {
    @IOField
    Integer inner1;
  }
  
  public static class Outer {
    @OneToOne
    private Inner reference1;
    
    @ManyToOne
    private Inner reference2;
  }
  
  @Test
  public void planFactoryTest () {
    Assert.assertNotNull(planFactory);
    IEntityPlan<Outer> plan = planFactory.getEntityPlan(Outer.class);
    INodePlan[] members = plan.getMembers();
    Assert.assertEquals(2, members.length);
    Assert.assertTrue(members[0] instanceof IReferencePlan);
    Assert.assertTrue(members[1] instanceof IReferencePlan);
  }
  
}
