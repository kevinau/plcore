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

import java.util.Arrays;

public class MimeType {
 
  private final String mimeType;
  
  private final String[] extensions;


  MimeType (String mimeType, String... extensions) {
    if (extensions.length == 0) {
      throw new IllegalArgumentException("A mime type must have one or more extensions");
    }
    this.mimeType = mimeType;
    this.extensions = extensions;
  }
  
  
  public String getExtension () {
    return extensions[0];
  }

  
  public String getMimeType () {
    return mimeType;
  }
  

  @Override
  public String toString() {
    return "MimeType [" + mimeType + ", " + Arrays.toString(extensions) + "]";
  }
  
}
