package org.plcore.srcdoc;

import com.sleepycat.persist.model.Persistent;

@Persistent
public class PageImage {

  private int imageIndex;
  private int imageWidth;
  private int imageHeight;
  
  
  public PageImage () {
  }

  
  public PageImage (int imageIndex, int imageWidth, int imageHeight) {
    this.imageIndex = imageIndex;
    this.imageWidth = imageWidth;
    this.imageHeight = imageHeight;
  }
  
  
  public int getImageIndex () {
    return imageIndex;
  }
  
  public int getWidth () {
    return imageWidth;
  }
  
 
  public int getHeight () {
    return imageHeight;
  }
  
}
