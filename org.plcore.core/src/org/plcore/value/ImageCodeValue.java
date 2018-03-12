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
