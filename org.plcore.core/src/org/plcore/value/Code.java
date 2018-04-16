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

import java.io.Serializable;

public class Code<T extends ICode<T>> implements ICode<T>, Serializable {

  private static final long serialVersionUID = -4174110261045376592L;

  private final String code;
  private final String description;
  private final boolean obsolete;
  private final boolean selfDescribing; 

  
	public Code (String code, String description, boolean obsolete) {
	  this.code = code;
		this.description = description;
		this.obsolete = obsolete;
    this.selfDescribing = code.equals(description);
	}
  
  
  public Code (String code, String description) {
    this (code, description, false);
  }
  
  
  public Code (String code) {
    this (code, code, false);
  }
  
  
  @Override
  public String getCode () {
    return code;
  }
  
  
  @Override
  public String getDescription () {
    return description;
  }
  
  
  @Override
  public boolean isSelfDescribing () {
    return selfDescribing;
  }
  
  
  @Override
  public boolean isObsolete() {
  	return obsolete;
  }

  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" + code + "," + description + ")";
  }
}
