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

import java.io.Serializable;

public class Code implements ICode, Serializable {

  private static final long serialVersionUID = -4174110261045376592L;

  private final String description;
  private final boolean obsolete;
  private final boolean selfDescribing; 

  
	public Code (String code, String description, boolean obsolete) {
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
    return super.toString();
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

}
