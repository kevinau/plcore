package org.plcore.userio.plan.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

import org.plcore.userio.EntryMode;
import org.plcore.userio.plan.IItemPlan;
import org.plcore.userio.plan.IListPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.MemberValueGetterSetter;
import org.plcore.userio.plan.PlanStructure;


public class ListPlan extends RepeatingPlan implements IListPlan {

  public ListPlan (PlanFactory planFactory, MemberValueGetterSetter field, Class<?> elemClass, String name, EntryMode entryMode, int dimension) {
    super (planFactory, field, elemClass, name, entryMode, dimension);
  }
  

  @Override
  public int getElementCount(Object value) {
    List<?> listValue = (List<?>)value;
    return listValue.size();
  }


  @Override
  public Object getElementValue(Object value, int i) {
    List<?> listValue = (List<?>)value;
    return listValue.get(i);
  }

  
  @SuppressWarnings("unchecked")
  @Override 
  public <X> Iterator<X> getIterator (Object instance) {
    List<X> list = (List<X>)instance;
    return list.iterator();
  }

    
  @Override
  public PlanStructure getStructure () {
    return PlanStructure.LIST;
  }

  
  @SuppressWarnings("unchecked")
  @Override
  public <X> X replicate(X fromValue) {
    List<?> fromList = (List<?>)fromValue;
    List<Object> toList = new ArrayList<>(fromList.size());
    for (Object v0 : fromList) {
      Object v1 = getElementPlan().replicate(v0);
      toList.add(v1);
    }
    return (X)toList;
  }


  @SuppressWarnings("unchecked")
  @Override
  public <X> X newInstance() {
    List<Object> instance = new ArrayList<>();
    return (X)instance;
  }
  
  
  @SuppressWarnings("unchecked")
  @Override
  public void walkNodes(Object value, BiConsumer<INodePlan, Object> consumer) {
    consumer.accept(this, value);
    
    INodePlan elementPlan = getElementPlan();
    List<Object> listValue = (List<Object>)value;
    for (Object elemValue : listValue) {
      elementPlan.walkNodes(elemValue, consumer);
    }
  }


  @SuppressWarnings("unchecked")
  @Override
  public void walkItems(Object value, BiConsumer<IItemPlan<?>, Object> consumer) {
    INodePlan elementPlan = getElementPlan();
    List<Object> listValue = (List<Object>)value;
    for (Object elemValue : listValue) {
      elementPlan.walkItems(elemValue, consumer);
    }
  }
  
}
