package org.plcore.userio.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.plcore.userio.model.INodeModel;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.model.ref.IValueReference;
import org.plcore.userio.model.ref.ListValueReference;
import org.plcore.userio.plan.IListPlan;

public class ListModel extends RepeatingModel {

  private final IListPlan listPlan;
  
  public ListModel (ModelFactory modelFactory, IValueReference valueRef, IListPlan listPlan) {
    super (modelFactory, valueRef, listPlan);
    this.listPlan = listPlan;
  }

  
  @SuppressWarnings("unchecked")
  @Override
  public List<?> setNew() {
    int minOccurs = listPlan.getMinOccurs();
    List<Object> value = new ArrayList<>(minOccurs);
    
    for (int i = 0; i < minOccurs; i++) {
      value.add(listPlan.newInstance());
    }
    syncValue(value, true);
    return value;
  }


  @Override
  public void syncValue(Object value, boolean setFieldDefault) {
    if (value == null) {
      for (INodeModel element : elements) {
        elements.remove(element);
      }
    } else {
      @SuppressWarnings("unchecked")
      List<Object> listValues = (List<Object>)value;
      valueRef.setValue(listValues);
      
      int i = 0;
      for (Object listValue : listValues) {
        INodeModel element;
        if (i < elements.size()) {
          element = elements.get(i);
        } else {
          IValueReference elementValueRef = new ListValueReference(valueRef, i);
          element = buildNodeModel(this, elementValueRef, listPlan.getElementPlan());
          elements.add(element);
        }
        element.syncValue(listValue, setFieldDefault);
        i++;
      }
    }
    
  }

}
