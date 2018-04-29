package org.plcore.userio.plan.impl;

import org.plcore.userio.EntryMode;
import org.plcore.userio.plan.EmbeddedLabelGroup;
import org.plcore.userio.plan.IAugmentedClass;
import org.plcore.userio.plan.IEmbeddedPlan;
import org.plcore.userio.plan.ILabelGroup;
import org.plcore.userio.plan.MemberValueGetterSetter;
import org.plcore.userio.plan.PlanStructure;


public class EmbeddedPlan<T> extends NameMappedPlan implements IEmbeddedPlan<T> {

  private final IAugmentedClass<T> aclass;
  private EmbeddedLabelGroup labels;
  
  
  public EmbeddedPlan (IAugmentedClass<T> aclass) {
    this(null, aclass, aclass.getSourceClass().getSimpleName(), entityEntryMode(aclass.getSourceClass()));
  }
  

  public EmbeddedPlan (MemberValueGetterSetter field, IAugmentedClass<T> aclass, String name, EntryMode entryMode) {
    super (field, aclass, name, entryMode);
    this.aclass = aclass;
    this.labels = new EmbeddedLabelGroup(field, name);
  }
  

  @Override
  public void dump(int level) {
    indent(level);
    System.out.println("EmbeddedPlan: " + getName());
    super.dump(level + 1);
  }

  
  @SuppressWarnings("unchecked")
  @Override
  public ILabelGroup getLabels () {
    return labels;
  }
  
//  @Override
//  public void accumulateTopItemPlans(List<IItemPlan<?>> fieldPlans) {
//    embeddedPlan.accumulateTopItemPlans(fieldPlans);
//  }
  
  
  @Override
  public PlanStructure getStructure () {
    return PlanStructure.EMBEDDED;
  }


  @SuppressWarnings("unchecked")
  @Override
  public <X> X newInstance() {
    return (X)aclass.newInstance();
  }


  @SuppressWarnings("unchecked")
  @Override
  public <X> X replicate(X fromValue) {
    return (X)aclass.replicate(fromValue);
  }

}
