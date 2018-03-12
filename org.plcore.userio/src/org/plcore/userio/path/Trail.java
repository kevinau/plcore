package org.plcore.userio.path;

import java.util.function.Consumer;

import org.plcore.userio.INode;

public class Trail {

  private final Trail parent;
  private final INode node;
  
  private boolean visited = false;
  

  Trail (Trail parent, INode node) {
    this.parent = parent;
    this.node = node;
  }
  
  void visitAll (Consumer<INode> consumer) {
    visited = true;
    if (parent != null && !parent.visited) {
      parent.visitAll(consumer);
    }
    consumer.accept(node);
  }
}

