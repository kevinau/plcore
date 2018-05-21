package org.plcore.dao.test;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;
import org.plcore.entity.VersionTime;

@Entity
public class InboxLog {

  @PrimaryKey(sequence = "InboxLog_ID")
  private int id = 0;
  
  private VersionTime versionTime;
  
  @SecondaryKey(relate = Relationship.ONE_TO_ONE)
  private String fileName;

  private String digest;

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
