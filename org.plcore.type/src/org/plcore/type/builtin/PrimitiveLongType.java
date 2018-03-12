package org.plcore.type.builtin;

import org.osgi.service.component.annotations.Component;
import org.plcore.type.IType;

@Component(service = IType.class)
public class PrimitiveLongType extends LongType {

  public PrimitiveLongType () {
  }
  
  
  public PrimitiveLongType (PrimitiveLongType type) {
    super (type);
  }
  
  
  @Override
  public boolean isPrimitive() {
    return true;
  }
  
  @Override
  public Class<?> getFieldClass() {
    return long.class;
  }

}
