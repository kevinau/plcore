package org.plcore.type.test;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.plcore.test.Assert;
import org.plcore.test.Before;
import org.plcore.test.Test;
import org.plcore.type.IType;
import org.plcore.type.TypeRegistry;
import org.plcore.type.builtin.IntegerType;

@Component
public class TypesAllTest {

  private TypeRegistry typeRegistry;
  
  @Before
  public void setup() {
    typeRegistry = new TypeRegistry();
    
    Map<String, Object> props = new HashMap<>();
    props.put("component.name", "integer");
    typeRegistry.addType(new IntegerType(), props);
  }
  
  @Test
  public void testTypeRegistry () {
    IType type = typeRegistry.getType(Integer.class);
    Assert.assertTrue(type instanceof IntegerType);
  }
}
