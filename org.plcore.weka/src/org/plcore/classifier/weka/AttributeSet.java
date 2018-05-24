package org.plcore.classifier.weka;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.plcore.srcdoc.ISegment;
import org.plcore.srcdoc.ISourceDocumentContents;
import org.plcore.srcdoc.SourceDocument;
import org.plcore.classifier.ClassSet;
import org.plcore.classifier.Dictionary;
import weka.core.Attribute;
import weka.core.Instances;

public class AttributeSet {

  private final Dictionary dictionary;
  
  private final ClassSet classSet;
  
  private int[] wordDocCounts = new int[512];
  
  private int documentCount = 0;
  
  
  public AttributeSet (Dictionary dictionary, ClassSet classSet) {
    this.dictionary = dictionary;  
    this.classSet = classSet;
  }
  
  
  /**
   * sets the file to use for training
   */
  void addDocument(SourceDocument d) throws Exception {
    String originName = d.getOriginName();
    String companyName = originName.substring(0, 3);
    classSet.add(companyName);
    
    ISourceDocumentContents dc = d.getContents();
    boolean[] wordOccurs = new boolean[wordDocCounts.length];

    List<? extends ISegment> segments = dc.getSegments();
    for (ISegment segment : segments) {
      switch (segment.getType()) {
      case CURRENCY :
      case DATE :
        // These are not considered for classification
        break;
      default :
        String word = segment.getText();
        if (word.length() > 2) {
          int i = dictionary.resolve(word);
          while (i >= wordDocCounts.length) {
            System.out.println("......... " + i + " " + wordDocCounts.length + "  " + word);
            wordDocCounts = Arrays.copyOf(wordDocCounts, wordDocCounts.length * 2);
            wordOccurs = Arrays.copyOf(wordOccurs, wordDocCounts.length);
          }
          wordOccurs[i] = true;              
        }
        break;
      }
    }
    // Increment the document count for words that appear in this document
    for (int i = 0; i < dictionary.size(); i++) {
      if (wordOccurs[i]) {
        wordDocCounts[i]++;
      }
    }
    documentCount++;
  }
  
  
  double[] calculateIdfs () {
    // Calculate idf's for all words in the dictionary
    double[] idfs = new double[dictionary.size()];
    for (int i = 0; i < dictionary.size(); i++) {
      idfs[i] = Math.log(documentCount / wordDocCounts[i]);
    }
        
    // Create the attribute information
    Attribute classAttribute = classSet.buildClassAttribute();
    ArrayList<Attribute> attributeList = new ArrayList<>(dictionary.size() + 1);
    for (String word : dictionary.words()) {
      Attribute attribute = new Attribute(word);
      attributeList.add(attribute);
    }
    attributeList.add(classAttribute);

    // Create the training data set
    instanceSet = new Instances("Training data", attributeList, documentCount);
    instanceSet.setClassIndex(instanceSet.numAttributes() - 1);
  }

}
