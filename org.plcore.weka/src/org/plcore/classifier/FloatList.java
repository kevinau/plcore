package org.plcore.classifier;

import java.util.Arrays;

public class FloatList {

  private float[] array;
  
  private int size = 0;
  
  public FloatList() {
    this (64);
  }
  
  
  public FloatList(int initialCapacity) {
    array = new float[initialCapacity];
  }
  
  
  public void set (int index, float value) {
    if (index >= array.length) {
      int capacity = array.length;
      while (index >= capacity) {
        capacity *= 2;
      }
      array = Arrays.copyOf(array, capacity);
    }
    if (index >= size) {
      size = index + 1;
    }
    array[index] = value;
  }
  
  
  public float get (int index) {
    if (index >= array.length) {
      return 0.0f;
    }
    return array[index];
  }
  
  
  public int size() {
    return size;
  }
  
}
