package org.plcore.userio.model.test;

import org.junit.jupiter.api.BeforeEach;
import org.plcore.type.TypeRegistry;
import org.plcore.userio.model.IModelFactory;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.plan.IPlanFactory;
import org.plcore.userio.plan.impl.PlanFactory;

public class ModelFactorySetup {

  protected IPlanFactory planFactory;
  
  protected IModelFactory modelFactory;
  
  @BeforeEach
  protected void setup () {
    TypeRegistry typeRegistry = new TypeRegistry();
    typeRegistry.addZeroConfigurationBuiltins();
    planFactory = new PlanFactory(typeRegistry);
    modelFactory = new ModelFactory(planFactory);
  }
  
}
