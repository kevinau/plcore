package org.plcore.userio.model.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.plcore.userio.INode;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.INameMappedModel;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.model.ItemEventAdapter;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.model.ref.ClassValueReference;
import org.plcore.userio.model.ref.IValueReference;
import org.plcore.userio.path.IPathExpression;
import org.plcore.userio.plan.INameMappedPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.IRuntimeDefaultProvider;


public abstract class NameMappedModel extends ContainerModel implements INameMappedModel {

  private final INameMappedPlan nameMappedPlan;
  
  private Map<String, INodeModel> members = new LinkedHashMap<>();
  
  
  public NameMappedModel (ModelFactory modelFactory, IValueReference valueRef, INameMappedPlan nameMappedPlan) {
    super (modelFactory, valueRef, nameMappedPlan);
    this.nameMappedPlan = nameMappedPlan;
    
//    // The model has now been constructed.  Set up the value change event handlers.
//    for (IRuntimeDefaultProvider defaultProvider : classPlan.getRuntimeDefaultProviders()) {
//      if (defaultProvider.isRuntime()) {
//        for (String dependsOn : defaultProvider.getDependsOn()) {
//          for (IItemModel itemModel : selectItemModels(dependsOn)) {
//            itemModel.addItemEventListener(new ItemEventAdapter() {
//              @Override
//              public void valueChange(INodeModel node) {
//                Object value = defaultProvider.getDefaultValue(valueRef.getValue());
//                ((IItemModel)node).setDefaultValue(value);
//              }
//            });
//          }
//        }
//      }
//    }

//    // In addition, run all the static default providers to set up
//    // the defaults.
//    for (IRuntimeDefaultProvider<? extends INode> defaultProvider : classPlan.getRuntimeDefaultProviders()) {
//      if (!defaultProvider.isRuntime()) {
//        Object defaultValue = defaultProvider.getDefaultValue(null);
//        for (IPathExpression<? extends INode> appliesTo : defaultProvider.getAppliesTo()) {
//          for (IItemModel itemModel : selectItemModels((IPathExpression<INodeModel>)appliesTo)) {
//            itemModel.setDefaultValue(defaultValue);
//          }
//        }
//      }
//    }
  }
  
  
  //@SuppressWarnings("unchecked")
  private void addRuntimeDefaultHandlers(IItemModel itemModel) {
    for (IRuntimeDefaultProvider defaultProvider : nameMappedPlan.getRuntimeDefaultProviders()) {
      if (defaultProvider.isRuntime()) {
        for (IPathExpression expr : defaultProvider.getDependsOn()) {
          if (itemModel.matches(this, expr)) {
            itemModel.addItemEventListener(new ItemEventAdapter() {
              @Override
              public void valueChange(INodeModel node) {
                Object value = defaultProvider.getDefaultValue(valueRef.getValue());
                for (IPathExpression appliesTo : defaultProvider.getAppliesTo()) {
                  List<IItemModel> appliesToModels = NameMappedModel.this.selectItemModels(appliesTo);
                  for (IItemModel appliesToModel : appliesToModels) {
                    appliesToModel.setDefaultValue(value);                    
                  }
                }
              }
            });
          }
        }
      }
    }
  }
  
  
  private void runRuntimeDefaults () {
    // Run all the runtime default providers to set up
    // the defaults.  After this setup, the runtime event handlers 
    // will keep them up to date.
    for (IRuntimeDefaultProvider defaultProvider : nameMappedPlan.getRuntimeDefaultProviders()) {
      // Check that all dependencies are error free
      boolean inError = false;
      loop:
      for (IPathExpression expr2 : defaultProvider.getDependsOn()) {
        List<IItemModel> dependents = selectItemModels((IPathExpression)expr2);
        for (IItemModel dependent : dependents) {
          if (dependent.isInError()) {
            inError = true;
            break loop;
          }
        }
      }
      if (!inError) {
        Object defaultValue = defaultProvider.getDefaultValue(getValue());

        for (IPathExpression expr : defaultProvider.getAppliesTo()) {
          List<IItemModel> targets = selectItemModels((IPathExpression)expr);
          for (IItemModel target : targets) {
            target.setDefaultValue(defaultValue);
          }
        }
      }
    }
  }
  
  
  @SuppressWarnings("unchecked")
  @Override
  public Object setNew () {
    Object newValue = nameMappedPlan.newInstance();
    syncValue(newValue, true);
    return newValue;
  }

  
  @Override
  public void syncValue (Object nameMappedValue, boolean setFieldDefault) {
    if (nameMappedValue == null) {
      INodePlan[] memberPlans = nameMappedPlan.getMembers();
      for (INodePlan memberPlan : memberPlans) {
        String fieldName = memberPlan.getName();
        INodeModel member = members.remove(fieldName);
        if (member != null) {
          fireChildRemoved(this, member);
        }
      }
    } else {
      valueRef.setValue(nameMappedValue);
      
      INodePlan[] memberPlans = nameMappedPlan.getMembers();
      for (INodePlan memberPlan : memberPlans) {
        String fieldName = memberPlan.getName();
        INodeModel member = members.get(fieldName);
        if (member == null) {
          IValueReference memberValueRef = new ClassValueReference(valueRef, memberPlan);
          member = buildNodeModel(this, memberValueRef, memberPlan);
          members.put(fieldName, member);
          if (member instanceof IItemModel) {
            addRuntimeDefaultHandlers((IItemModel)member);
          }
          fireChildAdded(this, member);
        }
        if (memberPlan.isViewOnly() == false) {
          Object memberValue = memberPlan.getFieldValue(nameMappedValue);
          member.syncValue(memberValue, setFieldDefault);
        }
      }
      runRuntimeDefaults();
    }
  }


  @Override
  public void dump(int level) {
    indent(level);
    System.out.println("NameMappedModel {");
    for (Map.Entry<String, INodeModel> member : members.entrySet()) {
      indent(level);
      //System.out.println(member.getKey() + ": ");
      System.out.println(member.getValue().getValueRefName() + ": ");
      member.getValue().dump(level + 1);
    }
    indent(level);
    System.out.println("}");
  }
  
  
  @SuppressWarnings("unchecked")
  @Override
  public <X extends INodeModel> X getMember(String name) {
    return (X)members.get(name);
  }

  
  @Override
  public INodeModel[] getMembers () {
    INodeModel[] result = new INodeModel[members.size()];
    int i = 0;
    for (INodeModel member : members.values()) {
      result[i++] = member;
    }
    return result;
  }


  @Override
  public Collection<INodeModel> getContainerNodes() {
    //return members.values();
    List<INodeModel> nodes = new ArrayList<>();
    for (Map.Entry<String, INodeModel> entry : members.entrySet()) {
      nodes.add(entry.getValue());
    }
    return nodes;
  }
  

  @SuppressWarnings("unchecked")
  @Override
  public INode getNameMappedNode(String name) {
    return (INode)members.get(name);
  }
  

  @Override
  public void walkModel(Consumer<INodeModel> before, Consumer<INodeModel> after) {
    before.accept(this);
    for (INodeModel member : members.values()) {
      member.walkModel(before, after);
    }
    after.accept(this);
  }
  
    
  @Override
  public void walkItems(Consumer<IItemModel> consumer) {
    for (INodeModel member : members.values()) {
      member.walkItems(consumer);
    }
  }
  
    
}
