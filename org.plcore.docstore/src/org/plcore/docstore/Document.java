package org.plcore.docstore;

import org.plcore.entity.VersionTime;
import org.plcore.userio.IOField;
import org.plcore.userio.ManyToOne;
import org.plcore.util.MimeType;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
public class Document {

  @PrimaryKey(sequence = "Document_ID")
  private int id;
  
  @SecondaryKey(relate = Relationship.ONE_TO_ONE)
  private String digest;
  
  private VersionTime version;
    
  private String originName;
  
  private MimeType mimeType;
  
  @ManyToOne
  private Party party;
  
  @IOField
  private IDocumentDetail documentDetail;
  
}
