package org.plcore.docstore.test;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.docstore.IDocumentStore;
import org.plcore.srcdoc.SourceDocument;

@Component(immediate = true)
public class SimpleTest {

  @Reference
  private IDocumentStore docStore;
  

  @Activate
  public void activate (ComponentContext componentContext) {
    Path path = Paths.get("C:/Users/Kevin/Accounts/JH Shares/Telstra/TLS_Dividend_Advice_2013_09_20.pdf");
    String hashCode = docStore.importDocument(path);
    System.out.println("...." + hashCode);
    Path source = docStore.getSourcePath(hashCode, ".pdf");
    System.out.println("...." + source.toAbsolutePath());
    
    SourceDocument foundDoc = docStore.getDocument(hashCode);
    System.out.println("...." + foundDoc);
  }
  
}
