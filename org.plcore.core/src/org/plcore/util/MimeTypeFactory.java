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
