package org.plcore.userio.plan;

import java.lang.reflect.Type;

public interface IInterfacePlan extends INodePlan {

  public Type getInterfaceType();
  
  public default boolean isInstance(Object value) {
    return ((Class<?>)getInterfaceType()).isInstance(value);
  }

}
