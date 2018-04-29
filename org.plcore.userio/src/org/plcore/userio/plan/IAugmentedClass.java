package org.plcore.userio.plan;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.plcore.userio.INode;

public interface IAugmentedClass<T> {

  public void complete(IPlanFactory planFactory);

  public Class<?> getSourceClass();

  public void addClassFields(IPlanFactory factory, Class<?> sourceClass, boolean b);

  public List<IRuntimeDefaultProvider> getRuntimeDefaultProviders();

  public List<IRuntimeValuesProvider> getRuntimeValuesProviders();

  public List<IRuntimeFactoryProvider> getRuntimeFactoryProviders();

  //public List<IRuntimeImplementationProvider> getRuntimeImplementationProviders();

  //public List<IRuntimeLabelProvider> getRuntimeLabelProviders();

  public List<IRuntimeModeProvider> getRuntimeModeProviders();

  public List<IRuntimeOccursProvider> getRuntimeOccursProviders();

  //public List<IRuntimeTypeProvider> getRuntimeTypeProviders();

  public Set<IValidationMethod> getValidationMethods();

  public String getClassName();

  public INodePlan[] getMembers();

  public <X extends INodePlan> X getMember(String name);

  public MemberValueGetterSetter getNodeField(String memberName);

  public Collection<? extends INode> getContainerNodes();

  public INode getNameMappedNode(String name);

  public void dump(int level);

  public T newInstance();

  public T replicate(Object value);

}
