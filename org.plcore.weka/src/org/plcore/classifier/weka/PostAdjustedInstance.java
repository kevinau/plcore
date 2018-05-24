package org.plcore.classifier.weka;

import weka.core.AbstractInstance;
import weka.core.Instance;

public class PostAdjustedInstance extends AbstractInstance {

  private static final long serialVersionUID = 1L;

  private Instance instance;
  
  private double[] idfs;
  

  public PostAdjustedInstance(Instance instance, double[] idfs) {
    this.instance = instance;
    this.idfs = idfs;
  }


  @Override
  public Instance copy(double[] values) {
    PostAdjustedInstance newInstance = (PostAdjustedInstance)instance.copy(values);
    newInstance.idfs = this.idfs;
    return newInstance;
  }


  @Override
  public int index(int arg0) {
    return instance.index(arg0);
  }


  @Override
  public Instance mergeInstance(Instance other) {
    Instance newInstance = instance.mergeInstance(other);
    double[] newIdfs = new double[idfs.length + ((PostAdjustedInstance)other).idfs.length];
    int m = 0;
    for (int i = 0; i < idfs.length; i++) {
      newIdfs[m] = idfs[i];
      m++;
    }
    for (int i = 0; i < ((PostAdjustedInstance)other).idfs.length; i++) {
      newIdfs[m] = ((PostAdjustedInstance)other).idfs[i];
      m++;
    }
    return new PostAdjustedInstance(newInstance, newIdfs);
  }


  @Override
  public int numAttributes() {
    return instance.numAttributes();
  }


  @Override
  public int numValues() {
    return instance.numValues();
  }


  @Override
  public void replaceMissingValues(double[] values) {
    instance.replaceMissingValues(values);
  }


  @Override
  public void setValue(int index, double value) {
    instance.setValue(index, value);
  }


  @Override
  public void setValueSparse(int index, double value) {
    instance.setValueSparse(index, value);
  }


  @Override
  public double[] toDoubleArray() {
    double[] values = instance.toDoubleArray();
    for (int i = 0; i < values.length; i++) {
      values[i] *= idfs[i];
    }
    return values;
  }


  @Override
  public String toStringNoWeight() {
    return instance.toStringNoWeight();
  }


  @Override
  public String toStringNoWeight(int arg0) {
    return instance.toStringNoWeight(arg0);
  }


  @Override
  public double value(int index) {
    return instance.value(index) * idfs[index];
  }


  @Override
  public Object copy() {
    Instance newInstance = (Instance)instance.copy();
    return new PostAdjustedInstance(newInstance, idfs);
  }


  @Override
  protected void forceDeleteAttributeAt(int arg0) {
    throw new RuntimeException("Method not implemented");
  }


  @Override
  protected void forceInsertAttributeAt(int arg0) {
    throw new RuntimeException("Method not implemented");
  }

}
