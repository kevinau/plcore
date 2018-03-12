package org.plcore.userio;

public interface INameMappedNode extends IContainerNode {

  public <X extends INode> X getNameMappedNode(String name);
  
}
