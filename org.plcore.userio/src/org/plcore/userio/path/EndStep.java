package org.plcore.userio.path;

import java.util.function.Consumer;

import org.plcore.userio.IContainerNode;
import org.plcore.userio.INode;


public class EndStep extends PathExpression {

  public EndStep (IPathExpression parent) {
    super(parent);
  }
  
  @Override
  public void dump(int level) {
    indent (level);
    System.out.println("<END>"+ "  (end)");
    super.dump(level + 1);
  }

  @Override
  public void matches(INode node, Trail trail, Consumer<INode> x) {
    super.matches(node, new Trail(trail, node), x);
  }


  @Override
  public void matches(INode node, INode target, boolean[] result) {
    if (node instanceof IContainerNode) {
      // Do nothing
    } else {
      super.matches(node, target, result);
    }
  }

}
