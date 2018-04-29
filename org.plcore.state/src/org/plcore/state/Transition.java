package org.plcore.state;

public class Transition {
  
  private final IState state;
  private final IAction action;
  private final TransitionFunction function;
  
  public Transition(IState state, IAction action, TransitionFunction function) {
    this.state = state;
    this.action = action;
    this.function = function;
  }

  public IState getState() {
    return state;
  }
  
  public IAction getAction() {
    return action;
  }
  
  
  public IState proceed() {
    return function.apply();
  }
  
}
