package org.plcore.state;

public class Action implements IAction {

  private final String name;
  
  private final String label;

  private final String description;

  private final boolean requiresValidEntry;

  public Action(String name) {
    this (name, null, null);
  }

  public Action(String name, boolean requiresValidEntry) {
    this (name, requiresValidEntry, null, null);
  }

  public Action(String name, String label, String description) {
    this (name, false, label, description);
  }

  public Action(String name, boolean requiresValidEntry, String label, String description) {
    this.name = name;
    this.requiresValidEntry = requiresValidEntry;
    this.label = label;
    this.description = description;
  }

  
  @Override
  public String name() {
    return name;
  }
  
  
  @Override
  public String getLabel() {
    return label;
  }


  @Override
  public String getDescription() {
    return description;
  }


  @Override
  public boolean requiresValidEntry() {
    return requiresValidEntry;
  }

  
  @Override
  public String toString() {
    return "Action[" + name + "," + requiresValidEntry + "]";
  }
  
}
