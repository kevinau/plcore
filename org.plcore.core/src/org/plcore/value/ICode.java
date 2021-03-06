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

public interface ICode<T> {

  public String getCode();
  
  public String getDescription();
  
  public default boolean isObsolete() {
    return false;
  }
  
  public default boolean isSelfDescribing() {
    return false;
  }
  
}
