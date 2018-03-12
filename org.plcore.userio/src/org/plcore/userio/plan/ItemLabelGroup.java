package org.plcore.userio.plan;

import java.util.Map;

import org.plcore.userio.Label;
import org.plcore.util.CamelCase;

public class ItemLabelGroup implements ILabelGroup {

  private final String label;

  private final String hint;

  private final String description;

  public ItemLabelGroup(String label, String hint, String description) {
    this.label = label;
    this.hint = hint;
    this.description = description;
  }

  public ItemLabelGroup(MemberValueGetterSetter field, String fieldName) {
    if (field == null) {
      label = CamelCase.toSentence(fieldName);
      hint = "";
      description = "";
    } else {
      Label labelAnn = field.getAnnotation(Label.class);
      if (labelAnn == null) {
        label = CamelCase.toSentence(fieldName);
        hint = "";
        description = "";
      } else {
        if (labelAnn.value().equals("\u0000")) {
          label = CamelCase.toSentence(fieldName);
        } else {
          label = labelAnn.value();
        }
        hint = labelAnn.hint();
        description = labelAnn.description();
      }
    }
  }

  public String getLabel() {
    return label;
  }

  public String getHint() {
    return hint;
  }

  public String getDescription() {
    return description;
  }
  

  @Override
  public void extractAll(Map<String, Object> context) {
    if (label != null && label.length() > 0) {
      context.put("label", label);
    }
    if (hint != null && hint.length() > 0) {
      context.put("hint", hint);
    }
    if (description != null && description.length() > 0) {
      context.put("description", description);
    }
  }

}
