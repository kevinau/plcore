package org.plcore.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class StateMachine {

  private IState state;
  
  private TransitionFunction startFunction;
  
  private TransitionFunction endFunction;
  
  private final List<Transition> transitions = new ArrayList<>();

  private final List<ActionChangeListener> actionChangeListeners = new ArrayList<>();

  private IAction[] actions;
  
  private boolean[] availableActions;
    
  
  public StateMachine () {
  }

  
  public void startFunction(TransitionFunction startFunction) {
    this.startFunction = startFunction;
  }
  
  
  public void endFunction(TransitionFunction endFunction) {
    this.endFunction = endFunction;
  }
  
  
  public void addTransition(IState state, IAction action, TransitionFunction function) {
    Transition transition = new Transition(state, action, function);
    transitions.add(transition);
  }
  
  
  public void addActionChangeListener(ActionChangeListener x) {
    actionChangeListeners.add(x);
  }


  public void removeActionChangeListener(ActionChangeListener x) {
    actionChangeListeners.remove(x);
  }


  private void fireActionChanged(IAction action, boolean available) {
    for (ActionChangeListener x : actionChangeListeners) {
      x.actionChanged(action, available);
    }
  }

  
  public IAction valueOf(String actionName) {
    for (IAction action : actions) {
      if (action.name().equals(actionName)) {
        return action;
      }
    }
    throw new IllegalArgumentException("Action not known: " + actionName);
  }

  
  public boolean requiresValidEntry(String actionName) {
    IAction action = valueOf(actionName);
    return action.requiresValidEntry();
  }
  
  
  private int indexOf(IAction action) {
    int i = 0;
    for (IAction a : actions) {
      if (a.equals(action)) {
        return i;
      }
      i++;
    }
    throw new IllegalArgumentException(action.toString());
  }
  
  
  private IAction[] findActions () {
    Map<String, IAction> actions = new LinkedHashMap<>();
    for (Transition t : transitions) {
      IAction action = t.getAction();
      actions.put(action.name(), action);
    }
    IAction[] array = new IAction[actions.size()];
    actions.values().toArray(array);
    return array;
  }

  
  /**
   * Set the initial state of this FSM.  This method fires action change events for
   * all actions that are available.
   */
  public void start() {
    this.actions = findActions();
    this.availableActions = new boolean[actions.length];
    
    state = startFunction.apply();

    // Clear all available actions
    Arrays.fill(availableActions, false);
    
    // Set actions that are available for this state
    for (Transition t : transitions) {
      if (t.getState() == state) {
        int i = indexOf(t.getAction());
        availableActions[i] = true;
      }
    }

    // Fire events for all actions that are 'not available'
    int i = 0;
    for (IAction a : actions) {
      if (!availableActions[i]) {
        fireActionChanged(a, false);
      }
      i++;
    }
    // Then fire event for all actions that are 'available'
    int j = 0;
    for (IAction a : actions) {
      if (availableActions[j]) {
        fireActionChanged(a, true);
      }
      j++;
    }
  }
  
  
  public void transition (IState onState, TransitionFunction function) {
    if (state == onState) {
      IState newState = function.apply();
      setState(newState);
    }
  }
  
  
  public void setState(IState state) {
    this.state = state;
    boolean[] priorActions = availableActions.clone();
    
    // Clear all available actions
    Arrays.fill(availableActions, false);
    
    for (Transition t : transitions) {
      if (t.getState() == state) {
        int i = indexOf(t.getAction());
        availableActions[i] = true;
      }
    }

    // Fire events for all actions that have changed to 'not available'
    int i = 0;
    for (IAction a : actions) {
      if (priorActions[i] && !availableActions[i]) {
        fireActionChanged(a, false);
      }
      i++;
    }
    // Then fire event for all actions that have changed to 'available'
    int j = 0;
    for (IAction a : actions) {
      if (availableActions[j] && !priorActions[j]) {
        fireActionChanged(a, true);
      }
      j++;
    }
  }
  
//  public void setAction(String actionName) {
//    for (IAction action : actions) {
//      if (action.name().equals(actionName)) {
//        // Find transition using the current state and the specified action
//        for (Transition t : transitions) {
//          if (t.getState() == state && t.getAction() == action) {
//            IState newState = t.proceed();
//            setState(newState);
//            return;
//          }
//        }
//        throw new IllegalArgumentException("No transition for: state " + state + ", action " + action);
//      }
//    }
//    throw new IllegalArgumentException("Action not known: " + actionName);
//  }
  
  
  public void finish() {
    if (endFunction != null) {
      endFunction.apply();
    }
  }
  
  
  public void invokeAction(String actionName) {
    for (IAction action : actions) {
      if (action.name().equals(actionName)) {
        // Find transition using the current state and the specified action
        for (Transition t : transitions) {
          if (t.getState() == state && t.getAction() == action) {
            IState newState = t.proceed();
            setState(newState);
            return;
          }
        }
        throw new IllegalArgumentException("No transition for: state " + state + ", action " + action);
      }
    }
    throw new IllegalArgumentException("Action not known: " + actionName);
  }
  
  
  public IState getState() {
    return state;
  }

}
