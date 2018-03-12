package org.plcore.type.builtin;

import org.osgi.service.component.annotations.Component;
import org.plcore.type.IType;

@Component(service = IType.class)
public class PrimitiveShortType extends ShortType {

  public PrimitiveShortType () {
  }
  
  
  public PrimitiveShortType (PrimitiveShortType type) {
    super (type);
  }
  
  
  @Override
  public boolean isPrimitive() {
    return true;
  }
  
  @Override
  public Class<?> getFieldClass() {
    return short.class;
  }

}
