package org.plcore.userio.model;


public class ReportableError {

  private final Object sourceRef;
  
  private final ErrorInstance errorInstance;
  
  public ReportableError (Object sourceRef, ErrorInstance errorInstance) {
    this.sourceRef = sourceRef;
    this.errorInstance = errorInstance;
  }
  
  @Override
  public String toString() {
    String x = sourceRef.toString();
    x += ": ";
    x += errorInstance.exception;
    for (IItemModel items : errorInstance.models) {
      x += " ";
      x += items.getName();
    }
    return x;
  }

}
