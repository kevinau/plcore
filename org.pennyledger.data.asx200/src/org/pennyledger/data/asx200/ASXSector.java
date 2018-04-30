package org.pennyledger.data.asx200;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class ASXSector {

  @PrimaryKey(sequence = "ASXSector_ID")
  int id;
  
  @SecondaryKey(relate = Relationship.ONE_TO_ONE)
  private String name;
  
  public ASXSector () {
  }
  
  public ASXSector (String name) {
    this.name = name;
  }
  
  @Override
  public String toString() {
    return "ASX20Sector[" + id + "," + name +"]";
  }

}
