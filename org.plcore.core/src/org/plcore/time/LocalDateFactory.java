/*******************************************************************************
 * Copyright (C) 2018 Kevin Holloway (kholloway@pennyledger.org)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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
