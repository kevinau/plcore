package org.plcore.userio.path;

import java.util.function.Consumer;

import org.plcore.userio.IContainerNode;
import org.plcore.userio.INode;

public class DescendentStep extends PathExpression implements IPathExpression {

  public DescendentStep (IPathExpression parent) {
    super(parent);
  }

  @Override
  public void dump(int level) {
    indent (level);
    System.out.println("**  (descendent)");
    super.dump(level + 1);
  }

  @Override
  public void matches(INode node, Trail trail, Consumer<INode> consumer) {
    matchDeep(node, trail, consumer);
  }
  
  
  private boolean matchDeep(INode node, Trail trail, Consumer<INode> consumer) {
    if (node instanceof IContainerNode) {
      IContainerNode container = (IContainerNode)node;
      for (INode child : container.getContainerNodes()) {
        super.matches(child, trail, consumer);
        matchDeep(child, trail, consumer);
      }
    }
    return true;
  }
  
  
  @Override
  public void matches(INode node, INode target, boolean[] result) {
    matchDeep(node, target, result);
  }
  
  
  private void matchDeep(INode node, INode target, boolean[] result) {
    if (node instanceof IContainerNode) {
      IContainerNode container = (IContainerNode)node;
      for (INode child : container.getContainerNodes()) {
        super.matches(child, target, result);
        if (result[0]) {
          return;
        }
        matchDeep(child, target, result);
      }
    }
  }
  

}
