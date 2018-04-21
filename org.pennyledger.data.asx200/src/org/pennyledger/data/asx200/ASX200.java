package org.pennyledger.data.asx200;

import org.plcore.math.Decimal;
import org.plcore.type.NumberSign;
import org.plcore.userio.IOField;

public class ASX200 {

  @IOField(pattern = "A-Z0-9{3}", length = 3)
  private String code;
  
  private String company;
  
  private String sector;
  
  @IOField(sign = NumberSign.UNSIGNED)
  private long marketCap;
  
  @IOField(precision = 5, scale = 2)
  private Decimal weight;
  
  @Override
  public String toString() {
    return "ASX200[" + code + "," + company + "," + sector + "," + marketCap + "," + weight +"]";
  }

}
