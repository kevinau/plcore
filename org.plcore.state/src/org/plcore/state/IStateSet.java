package org.plcore.state;

import java.util.HashSet;
import java.util.Set;

/**
 * An unordered set of states.
 */
public interface IStateSet {

  public void loadAll (Set<IState> set);
  
  public default Set<IState> getAll() {
    Set<IState> all = new HashSet<>(20);
    loadAll(all);
    return all;
  }
  
}
