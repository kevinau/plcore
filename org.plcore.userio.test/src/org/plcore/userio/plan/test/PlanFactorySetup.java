package org.plcore.userio.plan.test;

import org.junit.jupiter.api.BeforeEach;
import org.plcore.type.TypeRegistry;
import org.plcore.userio.plan.IPlanFactory;
import org.plcore.userio.plan.impl.PlanFactory;

public class PlanFactorySetup {

  private TypeRegistry typeRegistry;
  protected IPlanFactory planFactory;
  
  @BeforeEach
  public void setup () {
    typeRegistry = new TypeRegistry();
    typeRegistry.addZeroConfigurationBuiltins();
    planFactory = new PlanFactory(typeRegistry);
  }
  
}
