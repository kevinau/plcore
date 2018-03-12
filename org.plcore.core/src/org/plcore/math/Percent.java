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
