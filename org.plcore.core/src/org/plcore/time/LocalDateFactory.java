/*******************************************************************************
 * Copyright (C) 2018 Kevin Holloway (kholloway@pennyledger.org)
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.plcore.time;

import java.time.LocalDate;


public class LocalDateFactory {

  public static LocalDate parseDate (String source) throws IllegalArgumentException {
    String[] msg = new String[1];
    int[] resultYMD = new int[3];
    String[] completion = new String[1];
    int result = DateFactory.validate(source, null, msg, resultYMD, completion);
    if (result == DateFactory.OK) {
      return LocalDate.of(resultYMD[0], resultYMD[1], resultYMD[2]);
    } else {
      throw new IllegalArgumentException(source + ": " + msg[0]);
    }
  }

}
