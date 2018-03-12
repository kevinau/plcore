/*******************************************************************************
 * Copyright (C) 2018 Kevin Holloway (kholloway@pennyledger.org)
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.plcore.util;


public class RunTimer {

  private final long startTime;
  
  public RunTimer () {
    startTime = System.currentTimeMillis();
  }
  
  
  public void report () {
    long endTime = System.currentTimeMillis();
    long elapsedTime = endTime - startTime;
    System.out.println((elapsedTime / 1000.0) + " seconds");
  }
  
  
  public void report (String msg) {
    System.out.print(msg + ": ");
    report();
  }
}
