package org.plcore.userio.plan.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.plcore.userio.EntryMode;
import org.plcore.userio.INameMappedNode;
import org.plcore.userio.INode;
import org.plcore.userio.plan.IAugmentedClass;
import org.plcore.userio.plan.INameMappedPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.IRuntimeDefaultProvider;
import org.plcore.userio.plan.IRuntimeFactoryProvider;
//import org.plcore.userio.plan.IRuntimeImplementationProvider;
import org.plcore.userio.plan.IRuntimeModeProvider;
import org.plcore.userio.plan.IRuntimeOccursProvider;
import org.plcore.userio.plan.IRuntimeValuesProvider;
import org.plcore.userio.plan.IValidationMethod;
import org.plcore.userio.plan.MemberValueGetterSetter;


public abstract class NameMappedPlan extends ContainerPlan implements INameMappedPlan, INameMappedNode {

  private final IAugmentedClass<?> aclass;

  public NameMappedPlan (MemberValueGetterSetter field, IAugmentedClass<?> aclass, String name, EntryMode entryMode) {
    super(field, name, entryMode);
    this.aclass = aclass;
  }
  
  
  public void complete (PlanFactory factory) {
    aclass.addClassFields (factory, aclass.getSourceClass(), true);
  }
  
  
  @Override
  public String getClassName() {
    return aclass.getClassName();
  }
  
    
  @Override
  public List<IRuntimeModeProvider> getRuntimeModeProviders() {
    return aclass.getRuntimeModeProviders();
  }

  
  @Override
  public List<IRuntimeDefaultProvider> getRuntimeDefaultProviders() {
    return aclass.getRuntimeDefaultProviders();
  }

  
  @Override
  public List<IRuntimeValuesProvider> getRuntimeValuesProviders() {
    return aclass.getRuntimeValuesProviders();
  }

  
  @Override
  public List<IRuntimeFactoryProvider> getRuntimeFactoryProviders() {
    return aclass.getRuntimeFactoryProviders();
  }

  
  @Override
  public Set<IValidationMethod> getValidationMethods() {
    return aclass.getValidationMethods();
  }

  
  @Override
  public List<IRuntimeOccursProvider> getRuntimeOccursProviders() {
    return aclass.getRuntimeOccursProviders();
  }

  
  @Override
  public <X extends INodePlan> X getMember(String name) {
    return aclass.getMember(name);
  }


  @SuppressWarnings("unchecked")
  @Override
  public INode getNameMappedNode(String name) {
    return aclass.getNameMappedNode(name);
  }


  @Override
  public INodePlan[] getMembers() {
    return aclass.getMembers();
  }

  
  @Override
  public MemberValueGetterSetter getNodeField (String memberName) {
    return aclass.getNodeField(memberName);
  }
  
  
  @Override
  public void dump (int level) {
    aclass.dump(level);
  }


  @Override
  public Class<?> getSourceClass() {
    return aclass.getSourceClass();
  }


  @Override
  public Collection<? extends INode> getContainerNodes() {
    return aclass.getContainerNodes();
  }

}
