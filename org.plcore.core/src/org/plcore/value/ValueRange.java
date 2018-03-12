/*******************************************************************************
 * Copyright (C) 2018 Kevin Holloway (kholloway@pennyledger.org)
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.plcore.value;


public class ValueRange<T extends Comparable<T>> implements IValueRange<T> {

  private final T beginValue;
  private final T endValue;
  
  
  public ValueRange (T beginValue, T endValue) {
    this.beginValue = beginValue;
    this.endValue = endValue;
  }
  
  
  @Override
  public int compareToRange(T value) {
    int n = value.compareTo(beginValue);
    if (n < 0) {
      return -1;
    } else {
      n = value.compareTo(endValue);
      if (n < 0) {
        return 0;
      } else {
        return +1;
      }
    }
  }

  
  @Override
  public boolean contains(T obj) {
      return compareToRange(obj) == 0;
  }
  
  
  @Override
  public String toString () {
      return "[" + beginValue.toString() + ".." + endValue.toString() + ")";
  }


  @Override
  public T getBeginValue() {
    return beginValue;
  }


  @Override
  public T getEndValue() {
    return endValue;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + beginValue.hashCode();
    result = prime * result + endValue.hashCode();
    return result;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    @SuppressWarnings("unchecked")
    ValueRange<T> other = (ValueRange<T>)obj;
    if (!beginValue.equals(other.beginValue)) {
      return false;
    }
    if (!endValue.equals(other.endValue)) {
      return false;
    }
    return true;
  }

}
