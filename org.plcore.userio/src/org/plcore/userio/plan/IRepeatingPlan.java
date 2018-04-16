package org.plcore.userio.plan;

import java.util.Iterator;

import org.plcore.userio.IRepeatingMarkerNode;

public interface IRepeatingPlan extends IContainerPlan, IRepeatingMarkerNode {

  public int getDimension();
  
  public <X extends INodePlan> X getElementPlan();
  
  public Class<?> getElementClass();

  public int getMaxOccurs();

  public int getMinOccurs();
  
  public int getElementCount(Object value);

  public Object getElementValue(Object value, int i);

  public <X> Iterator<X> getIterator(Object value);

}
