package org.plcore.userio.plan.test;

import org.junit.Before;
import org.plcore.type.TypeRegistry;
import org.plcore.userio.plan.IPlanFactory;
import org.plcore.userio.plan.PlanFactory;

public class SimpleEntityPlanTest {

  private TypeRegistry typeRegistry;
  private IPlanFactory planFactory;
  
  @Before
  public void setup () {
    typeRegistry = new TypeRegistry();
    typeRegistry.addZeroConfigurationBuiltins();
    planFactory = new PlanFactory(typeRegistry);
  }
  
  @Test
  public void memberTest () {
    
  }
  
}
