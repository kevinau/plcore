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

import java.lang.reflect.Method;
import java.util.List;

import org.plcore.userio.plan.IRuntimeValuesProvider;
import org.plcore.value.ICode;


public class RuntimeValuesProvider extends RuntimeProvider implements IRuntimeValuesProvider {

  private final List<ICode> staticCodeValues;
  
  public RuntimeValuesProvider (Class<?> klass, FieldDependency fieldDependency, Method method, String[] appliesTo) {
    super (klass, fieldDependency, method, appliesTo);
    this.staticCodeValues = null;
  }

  
  public RuntimeValuesProvider (List<ICode> staticCodeValues, String[] appliesTo) {
    super (appliesTo);
    this.staticCodeValues = staticCodeValues;
  }

  
  public RuntimeValuesProvider (Class<?> klass, FieldDependency fieldDependency, Method method, String fieldName) {
    this (klass, fieldDependency, method, new String[] {fieldName});
  }
  
  
  /**
   * Get the list of code values for the target fields. The target fields are
   * those listed by the getAppliesTo method.
   */
   @Override
  public List<ICode> getCodeValues(Object instance) {
    if (isRuntime()) {
      if (instance == null) {
        throw new IllegalArgumentException();
      }
      return invokeRuntime(instance);
    } else {
      return staticCodeValues;
    }
  }

}
