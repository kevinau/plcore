package org.plcore.userio.plan.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.plcore.userio.EntryMode;
import org.plcore.userio.INode;
import org.plcore.userio.path.IPathExpression;
import org.plcore.userio.path.ParseException;
import org.plcore.userio.path.PathParser;
import org.plcore.userio.plan.IContainerPlan;
import org.plcore.userio.plan.IItemPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.MemberValueGetterSetter;

public abstract class ContainerPlan extends NodePlan implements IContainerPlan {

  public ContainerPlan(MemberValueGetterSetter field, String name, EntryMode entryMode) {
    super(field, name, entryMode);
  }

  
  @Override
  public List<INodePlan> selectNodePlans(String expr) {
    IPathExpression pathExpr;
    try {
      pathExpr = PathParser.parse(expr);
    } catch (ParseException ex) {
      throw new IllegalArgumentException(ex);
    }
    List<INodePlan> found = new ArrayList<>();
    pathExpr.matches(this, null, new Consumer<INode>() {

      @Override
      public void accept(INode node) {
        found.add((INodePlan)node);
      }
    });
    return found;
  }

  @Override
  public List<IItemPlan<?>> selectItemPlans(String expr) {
    IPathExpression pathExpr;
    try {
      pathExpr = PathParser.parse(expr);
    } catch (ParseException ex) {
      throw new IllegalArgumentException(ex);
    }

    List<IItemPlan<?>> found = new ArrayList<>();
    pathExpr.matches(this, null, new Consumer<INode>() {

      @Override
      public void accept(INode node) {
        if (node instanceof IItemPlan) {
          found.add((IItemPlan<?>)node);
        }
      }
    });
    return found;
  }

  @Override
  public INodePlan selectNodePlan(String expr) {
    List<INodePlan> found = selectNodePlans(expr);
    switch (found.size()) {
    case 0 :
      throw new IllegalArgumentException("No node plans matching: " + expr);
    case 1 :
      return found.get(0);
    default :
      throw new IllegalArgumentException(found.size() + " node plans matching: " + expr + ". I.e., more than one");
    }
  }

  @Override
  public IItemPlan<?> selectItemPlan(String expr) {
    List<IItemPlan<?>> found = selectItemPlans(expr);
    switch (found.size()) {
    case 0 :
      throw new IllegalArgumentException("No item plans matching: " + expr);
    case 1 :
      return found.get(0);
    default :
      throw new IllegalArgumentException(found.size() + " item plans matching: " + expr + ". I.e., more than one");
    }
  }

  
  @Override
  public String toString() {
    return "ContainerPlan[" + super.toString() + "]";
  }
  
}
