package org.plcore.type.builtin;

import org.osgi.service.component.annotations.Component;
import org.plcore.type.IType;

@Component(service = IType.class)
public class PrimitiveIntType extends IntegerType {

  public PrimitiveIntType () {
  }
  
  
  public PrimitiveIntType (PrimitiveIntType type) {
    super (type);
  }
  
  
  @Override
  public boolean isPrimitive() {
    return true;
  }
  
  @Override
  public Class<?> getFieldClass() {
    return int.class;
  }

  
}
