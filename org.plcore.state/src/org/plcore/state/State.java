package org.plcore.state;


public class State implements IState {

  private final String name;
  
  public State (String name) {
    this.name = name;
  }
  
  @Override
  public String toString() {
    return name;
  }
  
}
