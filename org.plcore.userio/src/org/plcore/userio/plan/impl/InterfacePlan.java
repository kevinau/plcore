package org.plcore.userio.plan.impl;

import java.lang.reflect.Type;

import org.plcore.userio.EntryMode;
import org.plcore.userio.plan.EmbeddedLabelGroup;
import org.plcore.userio.plan.IInterfacePlan;
import org.plcore.userio.plan.IPlanFactory;
import org.plcore.userio.plan.MemberValueGetterSetter;
import org.plcore.userio.plan.PlanStructure;


public class InterfacePlan extends NodePlan implements IInterfacePlan{

  @SuppressWarnings("unused")
  private final IPlanFactory planFactory;
  
  private final Type fieldType;
  private final EmbeddedLabelGroup labels;
  
  
  public InterfacePlan (IPlanFactory planFactory, MemberValueGetterSetter field, Type fieldType, String name, EntryMode entryMode) {
    super (field, name, entryMode);
    this.planFactory = planFactory;
    if (fieldType == null) { 
      throw new IllegalArgumentException("Type argument cannot be null");
    }
    this.fieldType = fieldType;
    this.labels = new EmbeddedLabelGroup(field, name);
  }
  

  @Override
  public Type getInterfaceType () {
    return fieldType;
  }
  
  
  @SuppressWarnings("unchecked")
  @Override
  public EmbeddedLabelGroup getLabels () {
    return labels;
  }
  
  
  @Override
  public void dump (int level) {
    indent(level);
    System.out.println("InterfacePlan("  + fieldType + "," + super.toString() + ")");
  }
  
  
  @Override
  public String toString() {
    return "InterfacePlan[type=" + fieldType + "," + super.toString() + "]";
  }
    
  
  @Override
  public PlanStructure getStructure () {
    return PlanStructure.INTERFACE;
  }


  @Override
  public <X> X replicate(X fromValue) {
    return fromValue;
  }
  
}
