package org.plcore.type.builtin;

import org.osgi.service.component.annotations.Component;
import org.plcore.type.IType;

@Component(service = IType.class)
public class PrimitiveCharType extends CharacterType {

  public PrimitiveCharType () {
  }
  
  
  public PrimitiveCharType (PrimitiveCharType type) {
    super (type);
  }
  
  
  @Override
  public boolean isPrimitive() {
    return true;
  }
  
  @Override
  public Class<?> getFieldClass() {
    return char.class;
  }

}
