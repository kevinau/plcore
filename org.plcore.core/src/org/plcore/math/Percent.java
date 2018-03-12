/*******************************************************************************
 * Copyright (C) 2018 Kevin Holloway (kholloway@pennyledger.org)
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.plcore.math;


public class Percent {

  private final Decimal value;
  
  public static final Percent ZERO = new Percent(0);
  
  
  public Percent (String source) {
    source = source.trim();
    if (source.endsWith("%")) {
      int n = source.length();
      source = source.substring(0, n - 1);
    }
    value = new Decimal(source);
  }

  
  public Percent (Decimal d) {
    value = d;
  }
  
  
  public Percent (int x) {
    value = new Decimal(x);
  }
  
  
  public Decimal multiply (Decimal amt) {
    return value.multiply(amt).divide(100);
  }


  public Decimal toDecimal() {
    return value;
  }
  
}
