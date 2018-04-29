package org.plcore.docstore;

import org.plcore.userio.Entity;

import com.sleepycat.persist.model.PrimaryKey;

@Entity
@com.sleepycat.persist.model.Entity
public class Party {

  @PrimaryKey(sequence = "Party_ID")
  private int id;
  
  private String name;
  
}
