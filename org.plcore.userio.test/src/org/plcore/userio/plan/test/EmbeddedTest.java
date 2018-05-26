package org.plcore.userio.plan.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.plcore.userio.Embeddable;
import org.plcore.userio.Embedded;
import org.plcore.userio.IOField;
import org.plcore.userio.plan.IEmbeddedPlan;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IItemPlan;
import org.plcore.userio.plan.INodePlan;


public class EmbeddedTest extends PlanFactorySetup {

  public static class Inner {
    @IOField
    Integer inner1;
  }
  
  @Embeddable
  public static class Inner2 {
    @IOField
    Integer inner2;
  }
  
  public static class Outer {
    @IOField
    Integer field1;
    
    @Embedded
    Inner firstInner;

    @Embedded
    Inner secondInner;
    
    Inner2 inner2;
  }
  
  @Test
  public void planFactoryTest () {
    Assertions.assertNotNull(planFactory);
    IEntityPlan<Outer> plan = planFactory.getEntityPlan(Outer.class);
    INodePlan[] members = plan.getMembers();
    Assertions.assertEquals(4, members.length);
    Assertions.assertTrue(members[0] instanceof IItemPlan);
    Assertions.assertTrue(members[1] instanceof IEmbeddedPlan);
    Assertions.assertTrue(members[2] instanceof IEmbeddedPlan);
    Assertions.assertTrue(members[3] instanceof IEmbeddedPlan);
  }
  
}
