package org.plcore.userio.path;

import java.util.function.Consumer;

import org.plcore.userio.INode;


public interface IPathExpression {
  
  public void dump(int level);

  public default void dump() {
    dump(0);
  }

  public default void indent(int level) {
    for (int i = 0; i < level; i++) {
      System.out.print("  ");
    }
  }

  public void matches(INode node, Trail trail, Consumer<INode> consumer);

  public void matches(INode node, INode target, boolean[] result);

  public void setNext(IPathExpression next);
  
  public IPathExpression getNext();

}
