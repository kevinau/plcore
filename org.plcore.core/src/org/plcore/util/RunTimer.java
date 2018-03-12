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
