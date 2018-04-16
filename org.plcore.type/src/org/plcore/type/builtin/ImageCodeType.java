/*******************************************************************************
 * Copyright (c) 2012 Kevin Holloway (kholloway@geckosoftware.co.uk).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 *******************************************************************************/
package org.plcore.type.builtin;


import java.util.List;

import org.plcore.value.IImageCodeValue;


public class ImageCodeType extends CodeType<IImageCodeValue> {
  
  
  public ImageCodeType (Class<IImageCodeValue> codeClass) {
    super (codeClass);
  }
  
  
  public ImageCodeType (Class<IImageCodeValue> codeClass, List<IImageCodeValue> valueList) {
    super (codeClass, valueList);
  }

  
  public ImageCodeType (ImageCodeType type) {
    super (type);
  }
  
  
  @Override
  public int getFieldSize () {
    return 0;
  }
  
}
