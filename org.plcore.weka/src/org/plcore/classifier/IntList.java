package org.plcore.classifier;

import java.util.Arrays;

public class IntList {

  private int[] array;
  
  private int size = 0;
  
  public IntList() {
    array = new int[64];
  }
  
  
  public IntList(int initialCapacity) {
    array = new int[initialCapacity];
  }
  
  
  public void set (int index, int value) {
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
  
  
  public void increment (int index) {
    if (index >= array.length) {
      set (index, 1);
    } else {
      array[index]++;
    }
  }
  
  
  public int get (int index) {
    if (index >= array.length) {
      return 0;
    }
    return array[index];
  }
  
  
  public int size() {
    return size;
  }
  
}
