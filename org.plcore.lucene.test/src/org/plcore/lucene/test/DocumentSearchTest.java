package org.plcore.lucene.test;

import java.util.Date;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.lucene.LuceneSearch;

@Component()
public class DocumentSearchTest {

  @Reference
  private LuceneSearch search;
  
  
  @Activate
  private void activate() {
    System.out.println("............ searching for Qantas");
    search.searchIndex("Qantas", doc -> {
      String id = doc.get("id");
      long datex = (long)(doc.getField("date").numericValue());
      System.out.println(id + " " + new Date(datex));
    });
    System.out.println("............");
  }
}
