package org.plcore.classifier;

import java.util.Arrays;

public class BooleanList {

  private boolean[] array;
  
  private int size = 0;
  
  public BooleanList() {
    array = new boolean[64];
  }
  
  
  public BooleanList(int initialCapacity) {
    array = new boolean[initialCapacity];
  }
  
  
  public void set (int index, boolean value) {
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
  
  
  public boolean get (int index) {
    if (index >= array.length) {
      return false;
    }
    return array[index];
  }
  
  
  public int size() {
    return size;
  }
  
}
