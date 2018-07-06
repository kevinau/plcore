package org.plcore.srcdoc;

import com.sleepycat.persist.model.Persistent;

@Persistent
public class SmallImage {

  private int imageIndex;
  private int imageWidth;
  private int imageHeight;
  private long digest;
  
  
  public SmallImage () {
  }

  
  public SmallImage (int imageIndex, int imageWidth, int imageHeight, long digest) {
    this.imageIndex = imageIndex;
    this.imageWidth = imageWidth;
    this.imageHeight = imageHeight;
    this.digest = digest;
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
  
  
  public long getDigest () {
    return digest;
  }
  
}
