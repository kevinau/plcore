/*******************************************************************************
 * Copyright (c) 2008 Kevin Holloway (kholloway@geckosoftware.com.au).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.txt
 * 
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 ******************************************************************************/
package org.plcore.userio.plan.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.plcore.userio.plan.IRuntimeOccursProvider;


public class RuntimeOccursProvider extends RuntimeProvider implements IRuntimeOccursProvider {

  private final int[][] sizes;
  
  
  public RuntimeOccursProvider (Class<?> klass, FieldDependency fieldDependency, Method method, String[] appliesTo) {
    super (klass, fieldDependency, method, appliesTo);
    this.sizes = new int[][] {
      {0, Integer.MAX_VALUE},
    };
  }

  
  public RuntimeOccursProvider (Class<?> klass, FieldDependency fieldDependency, Field field, String[] appliesTo) {
    super (klass, fieldDependency, field, appliesTo);
    this.sizes = new int[][] {
      {0, Integer.MAX_VALUE},
    };
  }

  
  public RuntimeOccursProvider (int[][] sizes, String[] appliesTo) {
    super (appliesTo);
    this.sizes = sizes;
  }

  
  public RuntimeOccursProvider (Class<?> klass, FieldDependency fieldDependency, Method method, String fieldName) {
    this (klass, fieldDependency, method, new String[] {fieldName});
  }
  
  
  public RuntimeOccursProvider (Class<?> klass, FieldDependency fieldDependency, Field field, String fieldName) {
    this (klass, fieldDependency, field, new String[] {fieldName});
  }
  
  
  static int[][] resolve (Object rawValue) {
    int[][] occurs;
    
    if (rawValue.getClass().isArray()) {
      Object[] rawValuex = (Object[])rawValue;
      Object rawValue1 = rawValuex[0];
      if (rawValue1.getClass().isArray()) {
        // The raw value is an array or (an array of 2 integers)
        occurs = (int[][])rawValue;
      } else {
        // The raw value is an array of 2 integers
        occurs = new int[1][2];
        occurs[0] = (int[])rawValue;
      }
    } else {
      // THe raw value is a single integer
      occurs = new int[1][2];
      occurs[0][0] = (int)rawValue;
      occurs[0][1] = (int)rawValue;
    }

    return occurs;
  }
  
  /**
   * Get the array size for the designated array fields. The designated fields are
   * those listed by the getAppliesTo method.
   */
  @Override
  public int[][] getOccurs(Object instance) {
    if (isRuntime()) {
      if (instance == null) {
        throw new IllegalArgumentException();
      }
      Object rawValue = invokeRuntime(instance);
      return resolve(rawValue);
    } else {
      return sizes;
    }
  }
  
  
  @Override 
  public String toString () {
    return "RuntimeOccursProvider [size=" + sizes + ", " + super.toString() + "]";
  }

}
