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

public class MimeTypeFactory {

  private static MimeType pdf = new MimeType("application/pdf", ".pdf");
  private static MimeType txt = new MimeType("text/plain", ".txt");
  private static MimeType html = new MimeType("text/html", ".html", ".htm");
  private static MimeType doc = new MimeType("application/msword", ".doc");
  
  private static MimeType[] mimeTypes = {
      pdf,
      txt,
      html,
      doc,
  };
  
  
  public MimeType getMimeType (String mimeType) {
    for (MimeType m : mimeTypes) {
      if (m.getMimeType().equals(mimeType)) {
        return m;
      }
    }
    throw new IllegalArgumentException("Unrecognised mime type: " + mimeType);
  }

}
