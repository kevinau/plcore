package org.plcore.test;

import java.util.HashMap;
import java.util.Map;

public class ResultSet implements IResultSet {

  private final Map<String, IResultSet> children = new HashMap<>();
  private final String name;
  
  private Status status = Status.OK;
  private Throwable throwable = null;
  
  
  public ResultSet () {
    this.name = null;
  }
  
  public ResultSet (String name) {
    this.name = name;
  }
  
  @Override
  public String getName() {
    return name;
  }
  
  
  @Override
  public IResultSet getResult(String name) {
    IResultSet results = children.get(name);
    if (results == null) {
      results = new ResultSet(name);
      children.put(name, results);
    }
    return results;
  }
  
  @Override
  public void setFailure (Throwable throwable) {
    this.status = Status.FAILURE;
    this.throwable = throwable;
  }
  
  @Override
  public void setOK () {
    this.status = Status.OK;
  }
  
  @Override
  public void setSkipped () {
    this.status = Status.SKIPPED;
  }
  
  @Override
  public boolean isFailure () {
    return throwable != null;
  }
  
  public void dump() {
    dump(0);
  }
  
  
  @Override
  public void dump (int n) {
    for (int i = 0; i < n; i++) {
      System.out.print("  ");
    }
    System.out.print(name);
    if (status != null) {
      switch (status) {
      case FAILURE :
        System.out.print(" \u2718");
        break;
      case OK :
        System.out.print(" \u2714");
        break;
      case SKIPPED :
        System.out.print(" -");
        break;
      }
    }
    System.out.println();
    if (throwable != null) {
      throwable.printStackTrace();
    }
    for (IResultSet rs : children.values()) {
      rs.dump(n + 1);
    }
  }
}
