package org.plcore.classifier;

import java.util.Arrays;

public class DoubleList {

  private double[] array;
  
  private int size = 0;
  
  public DoubleList() {
    this (64);
  }
  
  
  public DoubleList(int initialCapacity) {
    array = new double[initialCapacity];
  }
  
  
  public void set (int index, double value) {
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
  
  
  public void add (double value) {
    set(size, value);
  }
  
  
  public double get (int index) {
    if (index >= size) {
      return 0.0;
    }
    return array[index];
  }
  
  
  public double get (int index, double missingValue) {
    if (index >= size) {
      return missingValue;
    }
    return array[index];
  }
  
  
  public int size() {
    return size;
  }
  
  
  public void sort() {
    Arrays.sort(array, 0, size);
  }
  
}
