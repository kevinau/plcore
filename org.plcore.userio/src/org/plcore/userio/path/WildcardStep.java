package org.plcore.userio.path;

import java.util.function.Consumer;

import org.plcore.userio.IContainerNode;
import org.plcore.userio.INode;


public class WildcardStep extends PathExpression implements IPathExpression {

  public WildcardStep (IPathExpression parent) {
    super(parent);
  }
  
  @Override
  public void dump(int level) {
    indent(level);
    System.out.println("*  (wildcard path)");
    super.dump(level + 1);
  }


  @Override
  public void matches(INode node, Trail trail, Consumer<INode> consumer) {
    if (node instanceof IContainerNode) {
      IContainerNode container = (IContainerNode)node;
      for (INode child : container.getContainerNodes()) {
        super.matches(child, trail, consumer);
      }
    } else {
      throw new IllegalArgumentException("Wildcard (*) can only be applied to IContainerNode");
    }
  }


  @Override
  public void matches(INode node, INode target, boolean[] result) {
    if (node instanceof IContainerNode) {
      IContainerNode container = (IContainerNode)node;
      for (INode child : container.getContainerNodes()) {
        super.matches(child, target, result);
        if (result[0] == true) {
          break;
        }
      }
    } else {
      throw new IllegalArgumentException("Wildcard (*) can only be applied to IContainerNode");
    }
  }

}
