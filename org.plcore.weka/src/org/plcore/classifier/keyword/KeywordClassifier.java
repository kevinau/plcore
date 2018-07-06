package org.plcore.classifier.keyword;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.classifier.AgressiveTrim;
import org.plcore.classifier.BooleanList;
import org.plcore.classifier.ClassSet;
import org.plcore.classifier.Dictionary;
import org.plcore.classifier.DoubleList;
import org.plcore.classifier.FloatList;
import org.plcore.classifier.IntList;
import org.plcore.dao.IDataAccessObject;
import org.plcore.srcdoc.ISegment;
import org.plcore.srcdoc.ISourceDocumentContents;
import org.plcore.srcdoc.PageImage;
import org.plcore.srcdoc.SmallImage;
import org.plcore.srcdoc.SourceDocument;

import cern.colt.map.OpenIntIntHashMap;

@Component(immediate = true)
public class KeywordClassifier {

  @Reference(target = "(name=SourceDocument)")
  private IDataAccessObject<SourceDocument> sourceDocDAO;

  private Dictionary dictionary = new Dictionary(512);
  
  private ClassSet classSet = new ClassSet();

  FloatList[] termsFound = new FloatList[classSet.size()];

  
  @Activate
  private void activate (ComponentContext context) {
    List<String> keys = getOrderedKeys(sourceDocDAO);
    List<Integer> seen = new ArrayList<>();
    int trialCount = 0;
    int successCount = 0;
    
    for (String key : keys) {
      SourceDocument d = sourceDocDAO.getByPrimary(key);
      ISourceDocumentContents dc = d.getContents();

      String companyName = d.getOriginName().substring(0, 3);
      int docClass = classSet.resolve(companyName);
      
      System.out.println(">>>>>>>>>>>> " + d.getOriginName());
      int docClass2 = classify(dc);
      if (!seen.contains(docClass)) {
        System.out.println("First " + classSet.getValue(docClass));
        seen.add(docClass);
      } else {
        trialCount++;
        if (docClass == docClass2) {
          System.out.println("Correctly found " + classSet.getValue(docClass));
          successCount++;
        } else {
          System.out.println(d.getOriginName() + ": Incorrectly found " + classSet.getValue(docClass2) + " when expecting " + classSet.getValue(docClass));
        }
      }
      
      train (docClass, dc);
    }
    
    int x1 = (int)((successCount * 1000.0) / trialCount);
    double x = x1 / 10.0;
    System.out.println(successCount + " correctly classified out of " + trialCount + ": " + x + "%");
    
    // Dump keywords
//    IntIntProcedure proc = new IntIntProcedure() {
//      @Override
//      public boolean apply(int i, int count) {
//        int totalCount = termTotals.get(i);
//        if (count == totalCount) {
//          System.out.println("Keyword: " + dictionary.getWord(i) + "  (" + count + ")");
//          keywordCount++;
//        }
//        return true;
//      }
//    };
//
//    for (int c = 0; c < table.size(); c++) {
//      System.out.println("+++++++++++++++ " + c + " " + classSet.getValue(c));
//      
//      OpenIntIntHashMap row = table.get(c);
//      row.forEachPair(proc);
//    }
//    System.out.println("Keyword count " + keywordCount + " out of " + dictionary.size());

  }
  
  
  private List<String> getOrderedKeys (IDataAccessObject<SourceDocument> dao) {
    List<String> keys = new ArrayList<>();
    
    // Create instances
    dao.getAll(d -> {
      String name = d.getOriginName();
      int n = name.indexOf("_20");
      //if (name.startsWith("SGR")) {
      if (n == -1) {
        System.err.println("Cannot parse name: " + name);
      } else {
        String ymd = name.substring(n + 1, n + 11);
        keys.add(ymd + ":" + d.getHashCode());
      }
       //}
    });
    Collections.sort(keys);
    
    List<String> keys2 = new ArrayList<>();
    for (String key : keys) {
      keys2.add(key.substring(11));
    }
    return keys2;
  }


  private List<OpenIntIntHashMap> table = new ArrayList<>();
  private IntList termTotals = new IntList();
      
  
  private BooleanList identifyTerms(String stage, ISourceDocumentContents dc, FloatList weightInDoc) {
    BooleanList termInDoc = new BooleanList(dictionary.size() + 50);
    
    List<? extends ISegment> segments = dc.getSegments();
    for (ISegment segment : segments) {
      switch (segment.getType()) {
      case CURRENCY :
      case DATE :
        // These are not considered for classification
        break;
      case COMPANY_NUMBER :
        String conum = segment.getText();
        conum = AgressiveTrim.trim(conum).toLowerCase();
        int j = dictionary.resolve(conum);
        termInDoc.set(j, true);
        weightInDoc.set(j, Math.round(segment.getHeight()));
        break;        
      default :
        String phrase = segment.getText();
        phrase = AgressiveTrim.trim(phrase).toLowerCase();
        if (phrase.length() > 2) {
          int i = dictionary.resolve(phrase);
          termInDoc.set(i, true);
          weightInDoc.set(i, Math.round(segment.getHeight()));
        }
        break;
      }
    }

    List<SmallImage> smallImages = dc.getSmallImages();
    for (SmallImage smallImage : smallImages) {
      String imageName = "image" + smallImage.getDigest();
      int i = dictionary.resolve(imageName);
      System.out.println(">>>> small image: " + stage + ": " + imageName + "  " + i + "    " + smallImage.getHeight());
      termInDoc.set(i, true);
      weightInDoc.set(i, Math.round(smallImage.getHeight()));
    }
    return termInDoc;
  }
  
  
  private int classify (ISourceDocumentContents dc) {
    int size = dictionary.size();
    
    // The following may well expand the dictionary size
    FloatList weightInDoc = new FloatList();
    BooleanList termInDoc = identifyTerms("Classify", dc, weightInDoc);
    
    // For each term in the document...
//    int[] matches = new int[classSet.size()];
//    double[] matchWeights = new double[classSet.size()];

    DoubleList[] keywordsFoundByClass = new DoubleList[classSet.size()];
    for (int j = 0; j < keywordsFoundByClass.length; j++) {
      keywordsFoundByClass[j] = new DoubleList();
    }
    
    for (int i = 0; i < size; i++) {
      if (termInDoc.get(i)) {
        // see if it is a keyword for some docClass
        int termTotal = termTotals.get(i);
        System.out.println(">>   " + i + ", keyword " + dictionary.getWord(i) + "  weight " + weightInDoc.get(i));
        
        for (int j = 0; j < table.size(); j++) {
          OpenIntIntHashMap row = table.get(j);
          int count = row.get(i);
          if (count == termTotal) {
            keywordsFoundByClass[j].add(-weightInDoc.get(i));
//            System.out.println(">>   " + classSet.getValue(j) + ": " + i + ", keyword " + dictionary.getWord(i) + "  weight " + weightInDoc.get(i));
          }
//          matchWeights[j] = ((double)count) * weightInDoc.get(i) / termTotal;
        }
      }
    }
    
    for (int j = 0; j < keywordsFoundByClass.length; j++) {
      DoubleList keywordsFound = keywordsFoundByClass[j];

      // The keyword weightw are negative, so the largest comes first
      keywordsFoundByClass[j].sort();
      System.out.print(">>   " + classSet.getValue(j));
      for (int i = 0; i < keywordsFound.size(); i++) {
        System.out.print(" " + keywordsFound.get(i));
      }
      System.out.println(" ]]");
    }
    
    boolean[] candidates = new boolean[classSet.size()];
    Arrays.fill(candidates, true);
    int candidatesCount = candidates.length;
    int lastCandidateStanding = 0;
   
    System.out.println(">> initial: " + Arrays.toString(candidates));
    System.out.println(">> initial candidates count: " + candidatesCount + ">.... " + getBestClass(candidates));
    int i = 0;
    while (candidatesCount > 1) {      
      // Find the minimum (negative) keyword weight
      double minWeight = Double.MAX_VALUE;
      for (int j = 0; j < candidates.length; j++) {
        if (candidates[j]) {
          DoubleList keywordsFound = keywordsFoundByClass[j];
          double weight = keywordsFound.get(i, Double.NaN);
          System.out.println(classSet.getValue(j) + ".... " + weight + "     " + i + "/" + keywordsFound.size());
          if (Double.isNaN(weight)) {
            lastCandidateStanding = j;
            candidates[j] = false;
            candidatesCount--;
          } else {
            if (weight < minWeight) {
              minWeight = keywordsFound.get(i);
            }
          }
        }
      }
      
      System.out.println(">> interim: " + Arrays.toString(candidates));
      System.out.println(">> interim candidates count: " + candidatesCount + ">.... " + getBestClass(candidates));
      System.out.println(">> interim last standing: " + lastCandidateStanding + "   min weight " + minWeight);
      switch (candidatesCount) {
      case 0 :
        return lastCandidateStanding;
      case 1 :
        return getBestClass(candidates);
      default :
        // contine with the follwing elimination
      }
      
      // Eliminate those candidates that have a greater, negative weight
      for (int j = 0; j < candidates.length; j++) {
        if (candidates[j]) {
          DoubleList keywordsFound = keywordsFoundByClass[j];
          if (keywordsFound.get(i) != minWeight) {
            lastCandidateStanding = j;
            candidates[j] = false;
            candidatesCount--;
          }
        }
      }
      i++;
    }
    
    // Find the best class
    if (candidatesCount == 1) {
      return getBestClass(candidates);
    } else {
      return lastCandidateStanding;
    }
  }

  
  private int getBestClass (boolean[] candidates) {
    for (int j = 0; j < candidates.length; j++) {
      if (candidates[j]) {
        return j;
      }
    }
    return 0;
  }

  
  private void train (int docClass, ISourceDocumentContents dc) {
    FloatList weightInDoc = new FloatList();
    BooleanList termInDoc = identifyTerms("Train", dc, weightInDoc);
    
    OpenIntIntHashMap row;
    if (docClass >= table.size()) {
      row = new OpenIntIntHashMap();
      table.add(row);
    } else {
      row = table.get(docClass);
    }

    for (int i = 0; i < termInDoc.size(); i++) {
      if (termInDoc.get(i)) {
        int count = row.get(i);
        row.put(i, count + 1);
        termTotals.increment(i);
      }
    }
  }
}
