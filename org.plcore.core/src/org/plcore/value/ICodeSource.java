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

import java.util.List;


@FunctionalInterface
public interface ICodeSource<T extends ICode<T>> {

  public List<T> values();

  public default T valueOf (String code) {
    for (T value : values()) {
      if (value.getCode().equals(code)) {
        return value;
      }
    }
    return null;
  }
  
}
