package org.plcore.inbox;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class InboxSeen {

  @PrimaryKey
  String fileName;

  @SecondaryKey(relate = Relationship.MANY_TO_ONE)
  String digest;

  public InboxSeen() {
  }
  
  public InboxSeen(String fileName, String digest) {
    this.fileName = fileName;
    this.digest = digest;
  }
  
  @Override
  public String toString() {
    return "InboxSeen[" + fileName + "," + digest + "]";
  }

}
