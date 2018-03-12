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
