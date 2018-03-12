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
