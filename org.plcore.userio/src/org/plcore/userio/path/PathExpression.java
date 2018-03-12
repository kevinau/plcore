package org.plcore.userio.path;

import java.util.function.Consumer;

import org.plcore.userio.INode;


public class PathExpression implements IPathExpression {

  private final String source;
  private final IPathExpression parent;
  
  protected IPathExpression next = null;
  
  protected PathExpression (IPathExpression parent) {
    this.source = null;
    this.parent = parent;
    this.parent.setNext(this);
  }
    
  public PathExpression (String source) {
    this.source = source;
    this.parent = null;
  }
    
  
  @Override
  public void setNext(IPathExpression next) {
    this.next = next;
  }
  
  
  @Override
  public IPathExpression getNext() {
    return next;
  }
  
  
  @Override
  public void dump (int level) {
    if (next != null) {
      next.dump(level);
    }
  }
  

  @Override
  public void matches(INode node, Trail trail, Consumer<INode> consumer) {
    if (next != null) {
      next.matches(node, trail, consumer);
    } else {
      // We've reached the end of the path
      trail.visitAll(consumer);
    }
  }

  @Override
  public void matches(INode node, INode target, boolean[] result) {
    if (next != null) {
      next.matches(node, target, result);
    } else {
      // We've reached the end of the path
      result[0] = node.equals(target);
    }
  }
  
  @Override
  public String toString() {
    return source;
  }

}
