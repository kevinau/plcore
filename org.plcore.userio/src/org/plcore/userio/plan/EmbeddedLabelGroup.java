package org.plcore.userio.plan;

import java.util.Map;

import org.plcore.userio.EmbeddableLabel;
import org.plcore.userio.EmbeddedLabel;
import org.plcore.util.CamelCase;

public class EmbeddedLabelGroup implements ILabelGroup {

  private final String heading;

  private final String description;

  private final boolean headingFromAnn;
  
  private final boolean descriptionFromAnn;
  
  
  private EmbeddedLabelGroup(String heading, boolean headingFromAnn, String description, boolean descriptionFromAnn) {
    this.heading = heading;
    this.headingFromAnn = headingFromAnn;
    this.description = description;
    this.descriptionFromAnn = descriptionFromAnn;
  }

  
  public EmbeddedLabelGroup(MemberValueGetterSetter field, String fieldName) {
    if (field == null) {
      heading = CamelCase.toSentence(fieldName);
      headingFromAnn = false;
      description = "";
      descriptionFromAnn = false;
    } else {
      EmbeddedLabel labelAnn = field.getAnnotation(EmbeddedLabel.class);
      if (labelAnn == null) {
        heading = CamelCase.toSentence(fieldName);
        headingFromAnn = false;
        description = "";
        descriptionFromAnn = false;
      } else {
        if (labelAnn.heading().equals("\u0000")) {
          heading = CamelCase.toSentence(fieldName);
          headingFromAnn = false;
        } else {
          heading = labelAnn.heading();
          headingFromAnn = true;
        }
        if (labelAnn.description().equals("\u0000")) {
          description = "";
          descriptionFromAnn = false;
        } else {
          description = labelAnn.description();
          descriptionFromAnn = true;
        }
      }
    }
  }

  public String getHeading() {
    return heading;
  }

  public String getDescription() {
    return description;
  }

  
  public EmbeddedLabelGroup extendFromEmbeddable(Class<?> klass) {
    if (headingFromAnn && descriptionFromAnn) {
      return this;
    }
    EmbeddableLabel labelAnn = klass.getAnnotation(EmbeddableLabel.class);
    if (labelAnn == null) {
      return this;
    } else {
      String heading1;
      boolean heading1FromAnn;
      String description1;
      boolean description1FromAnn;

      if (headingFromAnn) {
        heading1 = heading;
        heading1FromAnn = headingFromAnn;
      } else if (labelAnn.heading().equals("\u0000")) {
        heading1 = heading;
        heading1FromAnn = false;
      } else {
        heading1 = labelAnn.heading();
        heading1FromAnn = true;
      }
      if (descriptionFromAnn) {
        description1 = description;
        description1FromAnn = descriptionFromAnn;
      } else if (labelAnn.description().equals("\u0000")) {
        description1 = description;
        description1FromAnn = false;
      } else {
        description1 = labelAnn.description();
        description1FromAnn = true;
      }
      return new EmbeddedLabelGroup(heading1, heading1FromAnn, description1, description1FromAnn);
    }
  }


  @Override
  public void extractAll(Map<String, Object> context) {
    if (heading != null && heading.length() > 0) {
      context.put("label", heading);
    }
    if (description != null && description.length() > 0) {
      context.put("description", description);
    }
  }

}
