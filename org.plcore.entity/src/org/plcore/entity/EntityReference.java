package org.plcore.entity;

import org.plcore.value.Code;

public class EntityReference extends Code<EntityReference> implements Comparable<EntityReference> {

  private static final long serialVersionUID = 1L;

  private final int id;
  
  private final EntityLife entityLife;
  
  
  public EntityReference (int id, String description, EntityLife entityLife) {
    super (description, entityLife != EntityLife.ACTIVE);
    this.id = id;
    if (description == null) {
      throw new IllegalArgumentException("description is null");
    }
    this.entityLife = entityLife;
  }
  
  
  public int getId () {
    return id;
  }
  
  
  public EntityLife getEntityLife () {
    return entityLife;
  }
  

  @Override
  public String toString() {
    return id + ": " + getDescription() + " " + entityLife;
  }


  @Override
  public int compareTo(EntityReference arg) {
    int n = getDescription().compareTo(arg.getDescription());
    if (n != 0) {
      return n;
    } else {
      return id - arg.id;
    }
  }


  @Override
  public String getCode() {
    return getDescription();
  }
  
}
