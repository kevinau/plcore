package org.plcore.userio.path;

import java.util.function.Consumer;

import org.plcore.userio.INameMappedNode;
import org.plcore.userio.INode;


public class NamedElementStep extends PathExpression {

  private final String name;
  
  public NamedElementStep (IPathExpression parent, String name) {
    super(parent);
    this.name = name;
  }
  
  @Override
  public void dump(int level) {
    indent (level);
    System.out.println(name + "  (named member)");
    super.dump(level + 1);
  }

  @Override
  public void matches(INode node, Trail trail, Consumer<INode> x) {
    if (node instanceof INameMappedNode) {
      INameMappedNode mapped = (INameMappedNode)node;
      INode member = mapped.getNameMappedNode(name);
      if (member == null) {
        // Do nothing
      } else {
        super.matches(member, trail, x);
      }
    } else {
      // Do nothing
    }
  }

  @Override
  public void matches(INode node, INode target, boolean[] result) {
    if (node instanceof INameMappedNode) {
      INameMappedNode mapped = (INameMappedNode)node;
      INode member = mapped.getNameMappedNode(name);
      if (member == null) {
        // Do nothing
      } else {
        super.matches(member, target, result);
      }
    } else {
      // Do nothing
    }
  }

}
