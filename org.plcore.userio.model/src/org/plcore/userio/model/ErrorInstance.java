package org.plcore.userio.model;

import org.plcore.type.UserEntryException;

public class ErrorInstance {

  public final IItemModel[] models;
  public final UserEntryException exception;

  public ErrorInstance (IItemModel[] models, UserEntryException exception) {
    this.models = models;
    this.exception = exception;
  }
  
}
