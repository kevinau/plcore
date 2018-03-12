package org.plcore.userio;

import java.util.EventListener;
import java.util.List;

import org.plcore.value.ICode;


public interface ValueSourceListener extends EventListener {

  public void sourceChanged (List<ICode> values);
  
}
