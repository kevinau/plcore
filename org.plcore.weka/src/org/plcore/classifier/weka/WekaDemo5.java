package org.plcore.classifier.weka;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.dao.IDataAccessObject;
import org.plcore.srcdoc.SourceDocument;

//@Component (immediate = true)
public class WekaDemo5 {
  
  @Reference(target = "(name=SourceDocument)")
  private IDataAccessObject<SourceDocument> sourceDocDAO;

  
  @Activate
  private void activate (ComponentContext context) throws Exception {
    List<String> keys = getOrderedKeys();
    Evaluator.Results results = new Evaluator.Results();
    for (int k = 1; k < keys.size() - 1; k++) {
      Evaluator evaluator = new Evaluator(sourceDocDAO, keys, k);
      evaluator.evaluate(results);
    }
    System.out.println(results);
  }

  
  private List<String> getOrderedKeys () {
    List<String> keys = new ArrayList<>();
    
    // Create instances
    sourceDocDAO.getAll(d -> {
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

}
