package org.plcore.lucene.loader.srcdoc;

import java.time.LocalDate;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.plcore.lucene.LuceneSearch;
import org.plcore.srcdoc.ISegment;
import org.plcore.srcdoc.ISourceDocumentContents;
import org.plcore.srcdoc.SourceDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(property = EventConstants.EVENT_TOPIC + "=" + "org/plcore/docstore/Document/*")
public class FromSourceDocument implements EventHandler {
 
  private Logger logger = LoggerFactory.getLogger(FromSourceDocument.class);
  
  @Reference
  private LuceneSearch luceneSearch;
  
  
  @Override
  public void handleEvent(Event event) {
    System.out.println("#################################### handle event " + event.getTopic());
    String topic = event.getTopic();
    if (topic.endsWith("/ADDED")) {
      System.out.println("........./ADDED");
      
      SourceDocument srcdoc = (SourceDocument)event.getProperty("document");
      System.out.println("......... " + srcdoc);
      
      Document indexedDoc = new Document();
      
      ISourceDocumentContents docContents = srcdoc.getContents();
      // Add document id
      String id = srcdoc.getHashCode();
      indexedDoc.add(new Field("id", id, StringField.TYPE_STORED));
      indexedDoc.add(new Field("source", "doc", StringField.TYPE_STORED));
      // ... and the date of the document
      long dateLong = srcdoc.getOriginTime().getTime();
      System.out.println("...........date " + dateLong);
      System.out.println("...........date " + LocalDate.ofEpochDay(dateLong/ (24 * 60 * 60 * 1000)));
      indexedDoc.add(new LongPoint("date", dateLong));
      indexedDoc.add(new StoredField("date", dateLong)); 
      // ... and finally all the text of the document.
      List<? extends ISegment> segments = docContents.getSegments();
      StringBuilder body = new StringBuilder();
      for (ISegment segment : segments) {
        body.append(segment.getText());
        body.append(' ');
      }
      indexedDoc.add(new Field("text", body.toString(), TextField.TYPE_NOT_STORED));

      logger.info("Indexing {} ({})", id, srcdoc.getOriginName());
      luceneSearch.addDocument(id, indexedDoc);
    } else if (topic.endsWith("/REMOVED")) {
      SourceDocument srcdoc = (SourceDocument)event.getProperty("document");
      String id = srcdoc.getHashCode();

      logger.info("De-indexing {} ({})", id, srcdoc.getOriginName());
      luceneSearch.remove(id);
    } else {
      throw new IllegalArgumentException("Topic not known: " + topic);
    }
  }
 
}
