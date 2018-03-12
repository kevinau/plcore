package org.plcore.userio;


public interface IRepeatingNode extends IContainerNode {

  @Override
  public int size();
  
  public INode getIndexedNode(int n);
  
}
