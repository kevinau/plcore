package org.plcore.classifier.keyword;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.classifier.Dictionary;
import org.plcore.dao.IDataAccessObject;
import org.plcore.srcdoc.ISegment;
import org.plcore.srcdoc.ISourceDocumentContents;
import org.plcore.srcdoc.SourceDocument;

import cern.colt.list.BooleanArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;

@Component(immediate = true)
public class KeywordClassifier {

  private Dictionary dictionary = new Dictionary(512);

  @Reference(target = "(name=SourceDocument)")
  private IDataAccessObject<SourceDocument> sourceDocDAO;

  @Activate
  private void activate (ComponentContext context) {
    List<String> keys = getOrderedKeys(sourceDocDAO);
    
    for (String key : keys) {
      SourceDocument d = sourceDocDAO.getByPrimary(key);
      ISourceDocumentContents dc = d.getContents();
      List<? extends ISegment> segments = dc.getSegments();
      
      int[] wordCounts = new int[dictionary.size()];
      for (ISegment segment : segments) {
        switch (segment.getType()) {
        case CURRENCY :
        case DATE :
          // These are not considered for classification
          break;
        default :
          String word = segment.getText();
          if (word.length() > 2) {
            int i = dictionary.query(word);
            if (i != -1) {
              wordCounts[i]++;
            }
          }
          break;
        }
      }
      
    }
  }
  
  
  private List<String> getOrderedKeys (IDataAccessObject<SourceDocument> dao) {
    List<String> keys = new ArrayList<>();
    
    // Create instances
    dao.getAll(d -> {
      String name = d.getOriginName();
      int n = name.indexOf("_20");
      if (n == -1) {
        System.err.println("Cannot parse name: " + name);
      } else {
        String ymd = name.substring(n + 1, n + 11);
        keys.add(ymd + ":" + d.getHashCode());
      }
    });
    Collections.sort(keys);
    
    List<String> keys2 = new ArrayList<>();
    for (String key : keys) {
      keys2.add(key.substring(11));
    }
    return keys2;
  }


  private List<OpenIntIntHashMap> table = new ArrayList<>();
  
  private void train (int docClass, List<? extends ISegment> segments) {
    BooleanArrayList termsInDoc = new BooleanArrayList(dictionary.size() + 50);
    
    for (ISegment segment : segments) {
      switch (segment.getType()) {
      case CURRENCY :
      case DATE :
        // These are not considered for classification
        break;
      default :
        String word = segment.getText();
        if (word.length() > 2) {
          int i = dictionary.query(word);
          if (i != -1) {
            termsInDoc.set(i,  true);
          }
        }
        break;
      }
    }
    
    OpenIntIntHashMap row = table.get(docClass);
    IntArrayList totalTermCount = new IntArrayList();
    totalTermCount.
    for (int i = 0; i < termsInDoc.size(); i++) {
      if (termsInDoc.get(i)) {
        int count = row.get(i);
        row.put(i, count + 1);
        count = totalTermCount.get(i);
        totalTermCount.set(i, count + 1);
      }
    }

    
  }
}
