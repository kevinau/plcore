package org.plcore.srcdoc;

import java.io.Serializable;
import java.sql.Timestamp;

public class SourceReference implements Serializable {

  private static final long serialVersionUID = 1L;

  
  private final String hashCode;

  private final Timestamp importTime;

  
  protected SourceReference (String hashCode, Timestamp importTime) {
    this.hashCode = hashCode;
    this.importTime = importTime;
  }
  
  
  public String getHashCode () {
    return hashCode;
  }
  
  
  public Timestamp getImportTime () {
    return importTime;
  }
  

  @Override
  public String toString() {
    return hashCode;
  }
  
}
