package org.plcore.userio.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.plcore.userio.INode;
import org.plcore.userio.model.ContainerChangeListener;
import org.plcore.userio.model.IContainerModel;
import org.plcore.userio.model.IEmbeddedModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.INameMappedModel;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.model.ref.IValueReference;
import org.plcore.userio.path.IPathExpression;
import org.plcore.userio.plan.IInterfacePlan;

public class InterfaceModel extends NodeModel implements INameMappedModel, ContainerChangeListener {
  
  private final ModelFactory modelFactory;
  private final IValueReference valueRef;
  private final IInterfacePlan interfacePlan;
  
  private final List<ContainerChangeListener> containerChangeListeners = new ArrayList<>();

  private IEmbeddedModel implementedModel = null;
  private Map<String, String> priorValues = new HashMap<>();
  
  
  public InterfaceModel (ModelFactory modelFactory, IValueReference valueRef, IInterfacePlan interfacePlan) {
    super (modelFactory, interfacePlan);
    this.modelFactory = modelFactory;
    this.valueRef = valueRef;
    this.interfacePlan = interfacePlan;
  }
  
  
  private void destroyOldImplementation () {
    // Save existing item field values
    implementedModel.walkItems(im -> {
      String qname = im.getQName();
      String svalue = im.getValueAsSource();
      priorValues.put(qname, svalue);
    });
    
    // Remove all children of the current NameMappedModel
    implementedModel.syncValue(null, false);

    // Remove container change event listener
    implementedModel.removeContainerChangeListener(this);
    
    implementedModel = null;
  }
  
  
  private void buildNewImplementation (Object newValue) {
    if (!interfacePlan.isInstance(newValue)) {
      throw new IllegalArgumentException("Value (" + newValue.getClass() + ") is not an instance of " + interfacePlan.getInterfaceType());
    }
 
    // Build new NamedMappedModel from new Value
    Class<?> newClass = newValue.getClass();
    implementedModel = modelFactory.buildEmbeddedModel(newClass);
    
    // Add container change event listener
    implementedModel.addContainerChangeListener(this);
    
    // TODO should we do this?
    implementedModel.setValue(newValue);

    // Restore any identically named item field values
    implementedModel.walkItems(im -> {
      String qname = im.getQName();
      String svalue = priorValues.get(qname);
      im.setValueFromSource(svalue);
    });
  }
  
  
  public void setValue (Object newValue) {
    Object oldValue = valueRef.getValue();
    if (newValue == null) {
      if (oldValue != null) {
        destroyOldImplementation();
      }
    } else {
      if (oldValue != null) {
        Class<?> oldClass = oldValue.getClass();
        Class<?> newClass = newValue.getClass();
        if (!oldClass.equals(newClass)) {
          destroyOldImplementation();
          buildNewImplementation(newValue);
        } else {
          // Sync the new values in the current NamedMappedModel
          implementedModel.syncValue(newValue, false);
        }
      } else {
        buildNewImplementation(newValue);
      }
    }
  }
  
  
  @Override
  public void dump() {
    System.out.println("EmbeddedModel:");
    super.dump();
  }


  @Override
  public INodeModel[] getMembers() {
    if (implementedModel != null) {
      return implementedModel.getMembers();
    } else {
      return new INodeModel[0];
    }
  }


  @Override
  public void addContainerChangeListener(ContainerChangeListener x) {
    containerChangeListeners.add(x);
  }

  @Override
  public void removeContainerChangeListener(ContainerChangeListener x) {
    containerChangeListeners.remove(x);
  }

  
  @Override
  public void fireChildAdded(IContainerModel parent, INodeModel node) {
    for (ContainerChangeListener x : containerChangeListeners) {
      x.childAdded(parent, node);
    }
    // Propagate the event upwards
    IContainerModel parentNode = getParent();
    if (parentNode != null) {
      parentNode.fireChildAdded(parent, node);
    }
  }

  
  @Override
  public void fireChildRemoved(IContainerModel parent, INodeModel node) {
    for (ContainerChangeListener x : containerChangeListeners) {
      x.childRemoved(parent, node);
    }
    // Propagate the event upwards
    IContainerModel parentNode = getParent();
    if (parentNode != null) {
      parentNode.fireChildRemoved(parent, node);
    }
  }

  
  @Override
  public List<INodeModel> selectNodeModels(String expr) {
    if (implementedModel != null) {
      return implementedModel.selectNodeModels(expr);
    } else {
      return Collections.emptyList();
    }
  }


  @Override
  public List<INodeModel> selectNodeModels(IPathExpression pathExpr) {
    if (implementedModel != null) {
      return implementedModel.selectNodeModels(pathExpr);
    } else {
      return Collections.emptyList();
    }
  }


  @Override
  public List<IItemModel> selectItemModels(String expr) {
    if (implementedModel != null) {
      return implementedModel.selectItemModels(expr);
    } else {
      return Collections.emptyList();
    }
  }


  @Override
  public List<IItemModel> selectItemModels(IPathExpression pathExpr) {
    if (implementedModel != null) {
      return implementedModel.selectItemModels(pathExpr);
    } else {
      return Collections.emptyList();
    }
  }


  @Override
  public <X extends INodeModel> X selectNodeModel(String expr) {
    if (implementedModel != null) {
      return implementedModel.selectNodeModel(expr);
    } else {
      throw new IllegalArgumentException("No node models matching: " + expr);
    }
  }


  @Override
  public <X extends INodeModel> X selectNodeModel(int id) {
    if (implementedModel != null) {
      return implementedModel.selectNodeModel(id);
    } else {
      throw new IllegalArgumentException("No node model for id: " + id);
    }
  }


  @Override
  public IItemModel selectItemModel(String expr) {
    if (implementedModel != null) {
      return implementedModel.selectItemModel(expr);
    } else {
      throw new IllegalArgumentException("No item model matching: " + expr);
    }
  }


  @Override
  public void addById(INodeModel node) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public <X extends INodeModel> X getById(int id) {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public <X> X setNew() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public Collection<? extends INodeModel> getContainerNodes() {
    if (implementedModel != null) {
      return implementedModel.getContainerNodes();
    } else {
      return Collections.emptyList();
    }
  }


  @Override
  public void buildQualifiedNamePart(StringBuilder builder, boolean[] isFirst, int[] repeatCount) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public String getValueRefName() {
    return valueRef.getName();
  }


  @Override
  public <X extends INode> X getNameMappedNode(String name) {
    if (implementedModel != null) {
      return implementedModel.getNameMappedNode(name);
    } else {
      return null;
    }
  }


  @Override
  public <X extends INodeModel> X getMember(String name) {
    if (implementedModel != null) {
      return implementedModel.getMember(name);
    } else {
      return null;
    }
  }


  @Override
  public void syncValue(Object value, boolean setFieldDefault) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public <T> T getValue() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public void dump(int level) {
    indent(level);
    System.out.println("InterfaceModel {" + interfacePlan.getInterfaceType());
    if (implementedModel != null) {
      implementedModel.dump(level + 1);
    }
    indent(level);
    System.out.println("}");
  }


  @Override
  public void childAdded(IContainerModel parent, INodeModel node) {
    fireChildAdded(parent, node);
  }


  @Override
  public void childRemoved(IContainerModel parent, INodeModel node) {
    fireChildRemoved(parent, node);
  }

}
