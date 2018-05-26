package org.plcore.userio.plan.test;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.plcore.userio.DefaultFor;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IRuntimeDefaultProvider;

public class ProvidersTest extends PlanFactorySetup {

  @SuppressWarnings("unused")
  public static class X {
    private int field1 = 123;
    
    private int field2 = 234;
    
    private int field3 = 345;
    
    private int field4 = 456;
    
    @DefaultFor({"field3", "field4"})
    private int sumDefault () {
      return field1 + field2;
    }
  }
  
  @Test
  public void planFactoryTest () {
    IEntityPlan<X> plan = planFactory.getEntityPlan(X.class);
    List<IRuntimeDefaultProvider> providers = plan.getRuntimeDefaultProviders();
    Assertions.assertEquals(1, providers.size());
  }
  
  
}
