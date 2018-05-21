package org.plcore.srcdoc;

import java.sql.Timestamp;

import org.osgi.service.component.annotations.Component;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;


@Entity
@Component (service = SourceDocument.class)
public class SourceDocument {

  @PrimaryKey
  private String hashCode;

  private Timestamp importTime;

  private Timestamp originTime;
  
  private String originName;

  private String originExtension;
  
  private ISourceDocumentContents contents;
  
  
  public SourceDocument() {
    this.contents = new SourceDocumentContents();
  }
  

  public SourceDocument(String hashCode, Timestamp originTime, String originName, String originExtension, Timestamp importTime, ISourceDocumentContents contents) {
    this.hashCode = hashCode;
    this.importTime = importTime;
    this.originTime = originTime;
    this.originName = originName;
    this.originExtension = originExtension;
    this.contents = contents;
  }

  
  public String getHashCode() {
    return hashCode;
  }
  
  
  public Timestamp getImportTime() {
    return importTime;
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

  
//  public void save(Path file) {
//    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
//      oos.writeObject(this);
//    } catch (IOException ex) {
//      throw new RuntimeException();
//    }
//  }
//
//  
//  public static SourceDocument load(Path file) {
//    SourceDocument document;
//    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.toFile()))) {
//      document = (SourceDocument)ois.readObject();
//    } catch (ClassNotFoundException | IOException ex) {
//      throw new RuntimeException(ex);
//    }
//    return document;
//  }


}
