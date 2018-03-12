package org.plcore.srcdoc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SourceDocument extends SourceReference implements Serializable {

  private static final long serialVersionUID = 2L;
  
  private static final Logger logger = LoggerFactory.getLogger(SourceDocument.class);

  private Timestamp originTime;
  
  private String originName;

  private String originExtension;
  
  private ISourceDocumentContents contents;
  

  public SourceDocument(String hashCode, Timestamp originTime, String originName, String originExtension, Timestamp importTime, ISourceDocumentContents contents) {
    super (hashCode, importTime);
    this.originTime = originTime;
    this.originName = originName;
    this.originExtension = originExtension;
    this.contents = contents;
  }

  
  public Timestamp getOriginTime () {
    return originTime;
  }
  
  
  public String getOriginName () {
    return originName;
  }
  
  
  public String getOriginExtension() {
    return originExtension;
  }
  
  
  public void setContents (ISourceDocumentContents contents) {
    this.contents = contents;
  }
  
  
  public ISourceDocumentContents getContents () {
    return contents;
  }
  
  
  @Override
  public String toString() {
    return "Document[" + super.toString() + ", " + originName + ", " + originExtension + ", " + originTime + "]";
  }

  
  public void save(Path file) {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
      oos.writeObject(this);
    } catch (IOException ex) {
      throw new RuntimeException();
    }
  }

  
  public static SourceDocument load(Path file) {
    SourceDocument document;
    logger.info("Loading source document: {}", file);
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.toFile()))) {
      document = (SourceDocument)ois.readObject();
    } catch (ClassNotFoundException | IOException ex) {
      throw new RuntimeException(ex);
    }
    return document;
  }


}
