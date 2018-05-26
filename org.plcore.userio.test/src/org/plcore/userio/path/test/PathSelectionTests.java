package org.plcore.userio.path.test;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.plcore.type.TypeRegistry;
import org.plcore.userio.Entity;
import org.plcore.userio.IOField;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IModelFactory;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.path.IPathExpression;
import org.plcore.userio.path.PathParser;
import org.plcore.userio.plan.IPlanFactory;
import org.plcore.userio.plan.impl.PlanFactory;

public class PathSelectionTests {

  public static Arguments[] data() {
      return new Arguments[] {
        Arguments.of("field1", 1, "Kevin"),
        Arguments.of("field2", 1, "Holloway"),
        Arguments.of("*", 5, null),
        Arguments.of("**", 9, null),
        Arguments.of("inner0/*", 0, null),
        Arguments.of("inner/inner1", 1, "0447 252 976"),
        Arguments.of("inner/inner1", 1, "0447 252 976"),
        Arguments.of("inner/*", 1, "0447 252 976"),
        Arguments.of("*/inner1", 1, "0447 252 976"),
        Arguments.of("**/inner1", 1, "0447 252 976"),
        Arguments.of("array/0", 1, "One"),
        Arguments.of("array/*", 3, null),
        Arguments.of("array/-1", 1, "Three"),
      };
  }

  @Entity
  public static class Inner0 {
  }
  
  @Entity
  public static class Inner {
    @IOField 
    private String inner1;
  }
  
  @Entity
  public static class Test1 {
    @IOField
    private String field1;
    @IOField
    private String field2;
    @IOField
    private Inner0 inner0;
    @IOField
    private Inner inner;
    @IOField 
    private String[] array;
  }
  

  private IEntityModel model;
    
  @BeforeEach
  public void setup() {
    TypeRegistry typeRegistry = new TypeRegistry();
    typeRegistry.addZeroConfigurationBuiltins();
    
    IPlanFactory planFactory = new PlanFactory(typeRegistry);
    
    IModelFactory modelFactory = new ModelFactory(planFactory);
    model = modelFactory.buildEntityModel(Test1.class);
    Test1 test1 = new Test1();
    test1.field1 = "Kevin";
    test1.field2 = "Holloway";
    Inner inner = new Inner();
    inner.inner1 = "0447 252 976";
    test1.inner = inner;
    test1.array = new String[] {
        "One",
        "Two",
        "Three",
    };
    model.setValue(test1);
  }
  
  
  @ParameterizedTest
  @MethodSource("data")
  public void nodePathSelect (String expr, int count, String found) {
    IPathExpression pathExpr = PathParser.parse(expr);
    
    // For coverage
    pathExpr.dump();
    
    List<INodeModel> nodes = model.selectNodeModels(pathExpr);
    Assertions.assertEquals(count, nodes.size());
    
    if (count == 1) {
      Assertions.assertEquals(found, nodes.get(0).getValue());
    }
  }


  @ParameterizedTest
  @MethodSource("data")
  public void nodePathMatches (String expr, int count, String found) {
    IPathExpression pathExpr = PathParser.parse(expr);
    List<INodeModel> nodes = model.selectNodeModels(pathExpr);
    
    if (count >= 1) {
      INodeModel node = nodes.get(0);
      boolean isMatched = node.matches(model, pathExpr);
      Assertions.assertTrue(isMatched, "Selected node " + node + " does not match " + pathExpr);
    }
  }

}
