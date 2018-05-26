package org.plcore.userio.plan.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.plcore.userio.IOField;
import org.plcore.userio.plan.IAugmentedClass;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.INodePlan;


public class PlanFactoryTest extends PlanFactorySetup {

  public static class X {
    @IOField
    private Integer field1;
  }
  
  @Test
  public void planFactoryTest () {
    IAugmentedClass<X> plan = planFactory.getAugmentedClass(X.class);

    // Get the same plan, they should be identical
    IAugmentedClass<X> plan2 = planFactory.getAugmentedClass(X.class);
    Assertions.assertSame(plan, plan2);
  }
  
  
  @SuppressWarnings("unused")
  public static class XX {
    private Integer field1;
    private Integer field2;
  }
  
  
  @Test
  public void multipleFieldTest () {
    IEntityPlan<XX> plan = planFactory.getEntityPlan(XX.class);
    INodePlan[] members = plan.getMembers();
    Assertions.assertEquals(2, members.length);
    
    Assertions.assertEquals("field1", members[0].getName());
    Assertions.assertEquals("field2", members[1].getName());
  }
  
}
