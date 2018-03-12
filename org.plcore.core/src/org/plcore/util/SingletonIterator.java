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

import java.util.Iterator;
import java.util.NoSuchElementException;


public class SingletonIterator<X> implements Iterator<X> {

  private boolean gotItem = false;
  private final X item;
  
  
  public SingletonIterator (X item) {
    this.item = item;
  }
  
  
  @Override
  public boolean hasNext() {
    return !this.gotItem;
  }

  @Override
  public X next() {
    if (this.gotItem) {
      throw new NoSuchElementException();
    }
    this.gotItem = true;
    return item;
  }

  @Override
  public void remove() {
    if (!this.gotItem) {
      this.gotItem = true;
    } else {
      throw new NoSuchElementException();
    }
  }

}
