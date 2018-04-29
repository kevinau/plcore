package org.plcore.userio.model;

import org.plcore.userio.model.impl.NameMappedModel;
import org.plcore.userio.model.ref.IValueReference;
import org.plcore.userio.plan.INameMappedPlan;

public class EmbeddedModel extends NameMappedModel {

  public EmbeddedModel(ModelFactory modelFactory, IValueReference valueRef, INameMappedPlan nameMappedPlan) {
    super(modelFactory, valueRef, nameMappedPlan);
  }

  @Override
  public void buildQualifiedNamePart(StringBuilder builder, boolean[] isFirst, int[] repeatCount) {
    super.buildQualifiedPlanName(builder);
  }

}
