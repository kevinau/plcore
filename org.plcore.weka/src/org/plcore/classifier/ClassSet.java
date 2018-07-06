package org.plcore.classifier;

import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;

public class ClassSet {

  private List<String> classValues = new ArrayList<>();
  
  
  public void add(String classValue) {
    if (!classValues.contains(classValue)) {
      classValues.add(classValue);
    }
  }
  
  
  public int resolve(String classValue) {
    int i = classValues.indexOf(classValue);
    if (i == -1) {
      i = classValues.size();
      classValues.add(classValue);
    }
    return i;
  }
  
  
  public Attribute buildClassAttribute() {
    List<String> classAttributeVector = new ArrayList<>();
    for (String value : classValues) {
      classAttributeVector.add(value);
    }
    classAttributeVector.add("?");
    return new Attribute("class", classAttributeVector);
  }
  
  
  public String getValue (int i) {
    return classValues.get(i);
  }
  
  
  public int indexOf(String classValue) {
    return classValues.indexOf(classValue);
  }
  
  
  public int size() {
    return classValues.size();
  }
  
}
