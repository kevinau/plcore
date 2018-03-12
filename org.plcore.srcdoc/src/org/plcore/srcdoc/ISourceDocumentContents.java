package org.plcore.srcdoc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

public interface ISourceDocumentContents {

  public List<? extends ISegment> getSegments();
  
  public int size();

  public void updateDictionary(Dictionary dictionary);


  public default void scaleSegments(double d) {
    if (d != 1.0) {
      throw new AbstractMethodError("Not implemented");
    }
  }


  public ISourceDocumentContents merge(ISourceDocumentContents other);


  public void sortSegments();


  public default void save(Path file) {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
      oos.writeObject(this);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }


  public static ISourceDocumentContents load(Path file) {
    ISourceDocumentContents docInstance;
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.toFile()))) {
      docInstance = (ISourceDocumentContents)ois.readObject();
    } catch (ClassNotFoundException | IOException ex) {
      throw new RuntimeException(ex);
    }
    return docInstance;
  }


  public void dump();

  public void addSegment (ISegment segment);
  
  public void addPageImage (PageImage docImage);
  
  public List<PageImage> getPageImages ();

  public int getPageCount();

  public ISegment getUniqueSegment(SegmentType type);
  
}
