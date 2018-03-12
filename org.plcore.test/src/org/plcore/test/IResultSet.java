package org.plcore.test;


public interface IResultSet {

  public static enum Status {
    OK,
    FAILURE,
    SKIPPED;
  }
  
  public String getName();
  
  public IResultSet getResult (String name);
  
  public void setFailure (Throwable throwable);
  
  public boolean isFailure ();
  
  public void dump (int n);

  public void setOK();
  
  public void setSkipped();
  
}
