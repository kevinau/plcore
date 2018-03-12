package org.plcore.userio.test.data;

import org.plcore.math.Decimal;
import org.plcore.userio.IOField;
import org.plcore.value.EntityLife;
import org.plcore.value.VersionTime;

public class SimpleEntity {

  @IOField
  private int id;

  @IOField
  private VersionTime version;
  
  @IOField
  private EntityLife entityLife;
  
  @IOField
  private String field1;
  
  @IOField
  private Decimal field2;
  
  public SimpleEntity () {
  }
  
  
  public SimpleEntity (String field1, Decimal field2) {
    this.id = 1;
    this.version = VersionTime.now();
    this.entityLife = EntityLife.ACTIVE;
    this.field1 = field1;
    this.field2 = field2;
  }
  
}
