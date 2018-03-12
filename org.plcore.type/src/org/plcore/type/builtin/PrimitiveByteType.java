package org.plcore.type.builtin;

import org.osgi.service.component.annotations.Component;
import org.plcore.type.IType;

@Component(service = IType.class)
public class PrimitiveByteType extends ByteType {

  public PrimitiveByteType () {
  }
  
  
  public PrimitiveByteType (PrimitiveByteType type) {
    super (type);
  }
  
  
  @Override
  public boolean isPrimitive() {
    return true;
  }
  
  @Override
  public Class<?> getFieldClass() {
    return byte.class;
  }

}
