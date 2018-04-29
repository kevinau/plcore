package org.plcore.state;

import java.util.Arrays;

/**
 * An unordered set of actions.
 */
public interface IActionSet {

  public IAction[] getAll();
  
  
  public default IAction[] concat(IAction... addnl) {
    return addnl;
  }
  
  public default IAction[] concat(IAction[] base, IAction... addnl) {
    IAction[] result = Arrays.copyOf(base, base.length + addnl.length);
    int i = base.length;
    for (IAction a : addnl) {
      String aname = a.name();
      int j = 0;
      for (IAction b : base) {
        if (b.name().equals(aname)) {
          result[j] = a;
          break;
        }
        j++;
      }
      if (j == base.length) {
        result[i++] = a;
      }
    }
    if (i == result.length) {
      return result;
    } else {
      return Arrays.copyOf(result, i);
    }
  }
  
}
