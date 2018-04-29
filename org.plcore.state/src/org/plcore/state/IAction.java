package org.plcore.state;


public interface IAction {

  public String name();
  
  public String getLabel();
  
  public String getDescription();
  
  public boolean requiresValidEntry();
  
}
