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



public class ImageCodeValue extends Code {

  private static final long serialVersionUID = 2216265814652748937L;

  private final Class<?> imageClass;
  private final String imageFileName;

//  protected static Image getImage (Class<?> imageClass, String imageFileName) {
//    this.imageClass = imageClass;
//    this.imageFileName = imageFileName;
//		ImageData source = new ImageData(klass.getResourceAsStream(imageFileName));
//		ImageData mask = source.getTransparencyMask();
//		return new Image(null, source, mask);
//  }
  

  public ImageCodeValue (String code, Class<?> imageClass, String imageFileName) {
    this (code, code, imageClass, imageFileName);
  }
  
  
  public ImageCodeValue (String code, String description, Class<?> imageClass, String imageFileName) {
    super (code, description);
    this.imageClass = imageClass;
    this.imageFileName = imageFileName;
  }
  
  
	public Class<?> getImageClass() {
		return imageClass;
	}
  
  
  public String getImageFileName() {
    return imageFileName;
  }
}
