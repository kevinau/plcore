package org.plcore.userio.test.data;

import org.plcore.entity.EntityLife;
import org.plcore.entity.VersionTime;
import org.plcore.math.Decimal;
import org.plcore.userio.IOField;

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
