package org.plcore.userio.plan.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.plcore.userio.EntryMode;
import org.plcore.userio.INameMappedNode;
import org.plcore.userio.INode;
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
import org.plcore.userio.plan.PlanFactory;


public abstract class NameMappedPlan<T> extends ContainerPlan implements INameMappedPlan, INameMappedNode {

  private final ClassPlan<T> classPlan;

  public NameMappedPlan (MemberValueGetterSetter field, ClassPlan<T> classPlan, String name, EntryMode entryMode) {
    super(field, name, entryMode);
    this.classPlan = classPlan;
  }
  
  
  public void complete (PlanFactory factory) {
    classPlan.addClassFields (factory, classPlan.getSourceClass(), true);
  }
  
  
  @Override
  public String getClassName() {
    return classPlan.getClassName();
  }
  
    
  @Override
  public List<IRuntimeModeProvider> getRuntimeModeProviders() {
    return classPlan.getRuntimeModeProviders();
  }

  
  @Override
  public List<IRuntimeDefaultProvider> getRuntimeDefaultProviders() {
    return classPlan.getRuntimeDefaultProviders();
  }

  
  @Override
  public List<IRuntimeValuesProvider> getRuntimeValuesProviders() {
    return classPlan.getRuntimeValuesProviders();
  }

  
  @Override
  public List<IRuntimeFactoryProvider> getRuntimeFactoryProviders() {
    return classPlan.getRuntimeFactoryProviders();
  }

  
  @Override
  public Set<IValidationMethod> getValidationMethods() {
    return classPlan.getValidationMethods();
  }

  
  @Override
  public List<IRuntimeOccursProvider> getRuntimeOccursProviders() {
    return classPlan.getRuntimeOccursProviders();
  }

  
  @Override
  public <X extends INodePlan> X getMember(String name) {
    return classPlan.getMember(name);
  }


  @SuppressWarnings("unchecked")
  @Override
  public INode getNameMappedNode(String name) {
    return classPlan.getNameMappedNode(name);
  }


  @Override
  public INodePlan[] getMembers() {
    return classPlan.getMembers();
  }

  
  @Override
  public MemberValueGetterSetter getNodeField (String memberName) {
    return classPlan.getNodeField(memberName);
  }
  
  
  @Override
  public void dump (int level) {
    classPlan.dump(level);
  }


  @Override
  public Class<?> getSourceClass() {
    return classPlan.getSourceClass();
  }


  @Override
  public <X> X newInstance () {
    return classPlan.newInstance();
  }
  
  
  @SuppressWarnings("unchecked")
  @Override
  public Object newInstance (Object fromInstance) {
    return classPlan.newInstance(fromInstance);
  }


  @Override
  public Collection<? extends INode> getContainerNodes() {
    return classPlan.getContainerNodes();
  }

}
