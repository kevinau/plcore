package org.plcore.srcdoc;

public interface ISegmentMatchResult {

  public int start();
  
  public int end();
  
  public SegmentType type();
  
  public Object value();
  
}
