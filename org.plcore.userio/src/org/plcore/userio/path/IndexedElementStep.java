package org.plcore.userio.path;

import java.util.function.Consumer;

import org.plcore.userio.INode;
import org.plcore.userio.IRepeatingNode;


public class IndexedElementStep extends PathExpression implements IPathExpression {

  /**
   * If the index is 'fromStart', it is 0 based.  If it is not 'fromStart' (i.e. it is from the end),
   * it is 1 based.  That is, 1 from end, is the last element.
   */
  private int index;
  
  private boolean fromStart;
  
  
  public IndexedElementStep (IPathExpression parent, int index, boolean fromStart) {
    super(parent);
    this.index = index;
    this.fromStart = fromStart;
  }
  
  public IndexedElementStep (IPathExpression parent, String s, boolean fromStart) {
    this (parent, Integer.parseInt(s), fromStart);
  }

  @Override
  public void dump(int level) {
    indent(level);
    if (fromStart) {
      System.out.println(index);
    } else {
      System.out.println(-index);
    }
    super.dump(level + 1);
  }

 
  @Override
  public void matches(INode node, Trail trail, Consumer<INode> x) {
    if (node instanceof IRepeatingNode) {
      IRepeatingNode repeating = (IRepeatingNode)node;
      int n = repeating.size();
      int i;
      if (fromStart) {
        i = index;
      } else {
        i = n - index;
      }
      if (i >= 0 && i < n) {
        INode element = repeating.getIndexedNode(i);
        super.matches(element, trail, x);
      }
    } else {
      throw new IllegalArgumentException("indexed element within a path expression, only applies to repeating nodes");
    }
  }

  
  @Override
  public void matches(INode node, INode target, boolean[] result) {
    if (node instanceof IRepeatingNode) {
      IRepeatingNode repeating = (IRepeatingNode)node;
      int n = repeating.size();
      int i;
      if (fromStart) {
        i = index;
      } else {
        i = n - index;
      }
      if (i >= 0 && i < n) {
        INode element = repeating.getIndexedNode(i);
        super.matches(element, target, result);
      }
    } else {
      throw new IllegalArgumentException("indexed element within a path expression, only applies to repeating nodes");
    }
  }

}
