package org.plcore.state;


@FunctionalInterface
public interface TransitionFunction {

  public IState apply();

}
