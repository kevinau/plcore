package org.plcore.type.builtin;

import org.osgi.service.component.annotations.Component;
import org.plcore.type.IType;

@Component(service = IType.class)
public class PrimitiveFloatType extends FloatType {

  public PrimitiveFloatType () {
  }
  
  
  public PrimitiveFloatType (PrimitiveFloatType type) {
    super (type);
  }
  
  
  @Override
  public boolean isPrimitive() {
    return true;
  }
  
  @Override
  public Class<?> getFieldClass() {
    return float.class;
  }

}
