package org.plcore.userio.model;

import org.plcore.userio.IEntityNode;

public interface IEmbeddedModel extends INameMappedModel, IEntityNode {

  public void setValue(Object value);
  
}
