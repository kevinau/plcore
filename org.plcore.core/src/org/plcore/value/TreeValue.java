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

public class TreeValue extends Code<TreeValue> {

  private static final long serialVersionUID = -3679834723224689234L;


  public TreeValue (String code, String label, boolean obsolete) {
		super(code, label, obsolete);
	}
  
  
  public TreeValue (String code, String label) {
    this (code, label, false);
  }
  
}
