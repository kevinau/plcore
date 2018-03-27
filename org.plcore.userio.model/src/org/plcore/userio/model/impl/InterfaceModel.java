package org.plcore.userio.model.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.plcore.type.UserEntryException;
import org.plcore.userio.EntryMode;
import org.plcore.userio.INode;
import org.plcore.userio.model.ContainerChangeListener;
import org.plcore.userio.model.EffectiveEntryMode;
import org.plcore.userio.model.EffectiveEntryModeListener;
import org.plcore.userio.model.IContainerModel;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.INameMappedModel;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.model.ItemEventListener;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.model.ref.IValueReference;
import org.plcore.userio.path.IPathExpression;
import org.plcore.userio.plan.IInterfacePlan;
import org.plcore.userio.plan.INodePlan;

public class InterfaceModel implements INameMappedModel {
  
  private final ModelFactory modelFactory;
  private final IValueReference valueRef;
  private final IInterfacePlan interfacePlan;
  
  private INameMappedModel implementedModel = null;
  private Map<String, String> priorValues = new HashMap<>();
  
  
  public InterfaceModel (ModelFactory modelFactory, IValueReference valueRef, IInterfacePlan interfacePlan) {
    super (modelFactory, valueRef, interfacePlan);
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
    implementedModel = null;
  }
  
  
  private void buildNewImplementation (Object newValue) {
    if (!interfacePlan.isInstance(newValue)) {
      throw new IllegalArgumentException("Value (" + newValue.getClass() + ") is not an instance of " + interfacePlan.getInterfaceType());
    }
 
    // Build new NamedMappedModel from new Value
    Class<?> newClass = newValue.getClass();
    INodePlan nodePlan = modelFactory.buildNodePlan(newClass);
    implementedModel = modelFactory.buildNodeModel(valueRef, nodePlan);
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


  @SuppressWarnings("unchecked")
  @Override
  public <X extends INodePlan> X getPlan() {
    if (implementedModel != null) {
      return implementedModel.getPlan();
    } else {
      return EMPTY_PLAN;
    }
  }


  @Override
  public void buildQualifiedNamePart (StringBuilder builder, boolean[] isFirst, int[] repeatCount) {
    if (isFirst[0] == false) {
      builder.append('.');
    }
    builder.append(getName());
    isFirst[0] = false;
    for (int i = 0; i < repeatCount[0]; i++) {
      builder.append("[]");
    }
    repeatCount[0] = 0;
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
    // TODO Auto-generated method stub
  }


  @Override
  public void removeContainerChangeListener(ContainerChangeListener x) {
    // TODO Auto-generated method stub
  }


  @Override
  public void fireChildAdded(IContainerModel parent, INodeModel node) {
    // TODO Auto-generated method stub
  }


  @Override
  public void fireChildRemoved(IContainerModel parent, INodeModel node) {
    // TODO Auto-generated method stub
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
    // TODO Auto-generated method stub
    return null;
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
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public int getNodeId() {
    // TODO Auto-generated method stub
    return 0;
  }


  @Override
  public void syncValue(Object value, boolean setFieldDefault) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void setParent(IContainerModel parent) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public <X> X getValue() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public void setEntryMode(EntryMode entryMode) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void updateEffectiveEntryMode(EffectiveEntryMode parent) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public EffectiveEntryMode getEffectiveEntryMode() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public void addEffectiveEntryModeListener(EffectiveEntryModeListener x) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void removeEffectiveEntryModeListener(EffectiveEntryModeListener x) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void addItemEventListener(ItemEventListener x) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void removeItemEventListener(ItemEventListener x) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void dump(int level) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public IContainerModel getParent() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public void walkModel(Consumer<INodeModel> before, Consumer<INodeModel> after) {
    if (implementedModel != null) {
      implementedModel.walkModel(before, after);
    }
  }


  @Override
  public void walkItems(Consumer<IItemModel> consumer) {
    if (implementedModel != null) {
      implementedModel.walkItems(consumer);
    }
  }


  @Override
  public void fireEffectiveModeChange(INodeModel node, EffectiveEntryMode priorMode) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void fireErrorNoted(INodeModel node, UserEntryException ex) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void fireErrorCleared(INodeModel node) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void fireSourceChange(INodeModel node) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void fireSourceEqualityChange(INodeModel node, boolean equal) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void fireValueChange(INodeModel node) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void fireValueEqualityChange(INodeModel node, boolean equal) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void fireComparisonBasisChange(INodeModel node) {
    // TODO Auto-generated method stub
    
  }


  @Override
  public IEntityModel getParentEntity() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public String getQName() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public String getQName(IContainerModel top) {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public String getValueRefName() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public String getQualifiedPlanName() {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public boolean matches(INodeModel startingPoint, IPathExpression expr) {
    // TODO Auto-generated method stub
    return false;
  }


  @Override
  public <X extends INode> X getNameMappedNode(String name) {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public <X extends INodeModel> X getMember(String name) {
    // TODO Auto-generated method stub
    return null;
  }

}
