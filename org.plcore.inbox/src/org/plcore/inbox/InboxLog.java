package org.plcore.inbox;

import org.plcore.entity.VersionTime;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
@Deprecated
public class InboxLog {

  @PrimaryKey(sequence = "InboxLog_ID")
  int id = 0;
  
  VersionTime versionTime;
  
  @SecondaryKey(relate = Relationship.ONE_TO_ONE)
  String fileName;

  @SecondaryKey(relate = Relationship.MANY_TO_ONE)
  String digest;

  public int getId() {
    return id;
  }
  
  public InboxLog() {
  }
  
  public InboxLog(String fileName, String digest) {
    this.fileName = fileName;
    this.digest = digest;
  }
  
  @Override
  public String toString() {
    return "InboxLog[" + id + "," + versionTime + "," + fileName + "," + digest + "]";
  }

}
