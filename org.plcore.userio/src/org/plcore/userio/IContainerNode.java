package org.plcore.userio;

import java.util.Collection;

public interface IContainerNode extends INode {

  public Collection<? extends INode> getContainerNodes();
  
  public default int size() {
    return getContainerNodes().size();
  }
  
}
