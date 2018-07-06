package org.plcore.classifier;

import java.lang.reflect.Array;
import java.util.Arrays;

public class ObjectList<T> {

  private final Class<T> elemClass;
  
  private T[] array;
  
  private int size = 0;
  
  public ObjectList(Class<T> elemClass) {
    this(elemClass, 64);
  }
  
  
  @SuppressWarnings("unchecked")
  public ObjectList(Class<T> elemClass, int initialCapacity) {
    this.elemClass = elemClass;
    array = (T[])Array.newInstance(elemClass, initialCapacity);
  }
  
  
  public void set (int index, T value) {
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
  
  
  public T get (int index) {
    if (index >= array.length) {
      try {
        return (T)elemClass.newInstance();
      } catch (InstantiationException | IllegalAccessException ex) {
        throw new RuntimeException(ex);
      }
    }
    return array[index];
  }
  
  
  public int size() {
    return size;
  }
  
}
