package org.plcore.userio.plan;

import java.util.Map;

import org.plcore.userio.IIndexLabelProvider;
import org.plcore.userio.NumberedIndexLabelProvider;
import org.plcore.userio.RepeatingLabel;
import org.plcore.util.CamelCase;


public class RepeatingLabelGroup implements ILabelGroup {

  private final String label;

  private final String description;

  private final IIndexLabelProvider indexLabelProvider;


  public RepeatingLabelGroup() {
    this("", "");
  }


  public RepeatingLabelGroup(String label, String description) {
    this.label = label;
    this.description = description;
    this.indexLabelProvider = new NumericIndexLabelProvider();
  }


  public RepeatingLabelGroup(MemberValueGetterSetter field, String fieldName) {
    RepeatingLabel labelAnn = field.getAnnotation(RepeatingLabel.class);
    if (labelAnn == null) {
      label = CamelCase.toSentence(fieldName);
      description = "";
      indexLabelProvider = new NumberedIndexLabelProvider();
    } else {
      if (labelAnn.value().equals("\u0000")) {
        label = CamelCase.toSentence(fieldName);
      } else {
        label = labelAnn.value();
      }
      Class<? extends IIndexLabelProvider> labelClass = labelAnn.indexLabels();
      try {
        indexLabelProvider = labelClass.newInstance();
      } catch (InstantiationException | IllegalAccessException ex) {
        throw new RuntimeException("Index label provider '" + labelClass.getName() + "' not found", ex);
      }
      description = labelAnn.description();
    }
  }


  public String getLabel() {
    return label;
  }


  public String getDescription() {
    return description;
  }


  public String getIndexLabel(int index) {
    return indexLabelProvider.getIndexLabel(index);
  }


  @Override
  public void extractAll(Map<String, Object> context) {
    if (label != null && label.length() > 0) {
      context.put("label", label);
    }
    if (description != null && description.length() > 0) {
      context.put("description", description);
    }
    if (indexLabelProvider != null) {
      context.put("indexLabelProvider", indexLabelProvider);
    }
  }

}
