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


/** 
 * A month within a year.  Years are 4 digits, representing the full year.
 * Months are numbered starting with 1.
 * 
 * @author Kevin Holloway
 */
public class YearMonth {

  private final int index;
  
  public YearMonth (int year, int month) {
    index = year * 12 + month - 1;
  }
  
  
  public int getYear () {
    return index / 12;
  }
  
  
  public int getMonth () {
    return index % 12 + 1;
  }
}
