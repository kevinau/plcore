package org.pennyledger.data.asx200;

import org.plcore.userio.IOField;

public class ASXSector {

  @IOField(length = 30)
  private String name;
  
  @Override
  public String toString() {
    return "ASX20Sector[" + name +"]";
  }

}
