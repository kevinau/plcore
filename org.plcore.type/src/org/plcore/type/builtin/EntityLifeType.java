package org.plcore.type.builtin;

import org.osgi.service.component.annotations.Component;
import org.plcore.type.IType;
import org.plcore.value.EntityLife;

@Component(service = IType.class)
public class EntityLifeType extends EnumType<EntityLife> {

  public EntityLifeType () {
    super(EntityLife.class);
  }

  
  public EntityLifeType (EntityLifeType type) {
    super (type);
  }
  
  
  @Override
  public Class<?> getFieldClass () {
    return EntityLife.class;
  }
  
}
