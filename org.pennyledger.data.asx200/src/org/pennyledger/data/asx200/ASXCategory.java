package org.pennyledger.data.asx200;

import org.osgi.service.component.annotations.Component;
import org.plcore.entity.IEntity;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
@Component
public class ASXCategory implements IEntity {

  @PrimaryKey(sequence = "ASXCategory_ID")
  int id;
  
  @SecondaryKey(relate = Relationship.ONE_TO_ONE)
  private String name;
  
  public ASXCategory () {
  }
  
  public ASXCategory (String name) {
    this.name = name;
  }
  
  @Override
  public String toString() {
    return "ASXCategory[" + id + "," + name +"]";
  }

}
