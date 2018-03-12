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

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;

public interface DigestFactory {
  
  public Digest getFileDigest (File file);
  
  public Digest getFileDigest (Path path);
  
  public Digest getFileDigest (URL url);
  
  public Digest getInputStreamDigest (InputStream fis);
  
  public Digest getObjectDigest (Object obj);

}
