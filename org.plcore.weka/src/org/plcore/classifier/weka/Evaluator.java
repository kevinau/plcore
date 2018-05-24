package org.plcore.classifier.weka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.plcore.classifier.ClassSet;
import org.plcore.classifier.Dictionary;
import org.plcore.dao.IDataAccessObject;
import org.plcore.srcdoc.ISegment;
import org.plcore.srcdoc.ISourceDocumentContents;
import org.plcore.srcdoc.SourceDocument;

import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Randomize;

public class Evaluator {
  
  private static final int INITIAL_DICTIONARY_SIZE = 512;
  
  public static class Results {
    int success;
    int total;
    
    @Override
    public String toString() {
      double x = (double)success * 100 / total;
      int j = (int)(x * 10 + 0.1);
      x = j / 10.0;
      return success + " out of " + total + ", " + x + "%";
    }
  }
  
  private IDataAccessObject<SourceDocument> sourceDocDAO;

  /** the classifier used internally */
  private NaiveBayes classifier = null;

  /** the filter to use */
  private Filter filter = null;

  private int documentCount = 0;

  private int[] wordDocCounts = new int[INITIAL_DICTIONARY_SIZE];

  private Dictionary dictionary;

  private ClassSet classSet;
  
  private double[] idfs;
  
  /** the training instances */
  private Instances instanceSet = null;

  private Instances filtered = null;
  
  private List<String> keys;
  private int trainingCount;
  
  private List<String> seen = new ArrayList<>();
  private Results results = new Results();

  
  public Evaluator (IDataAccessObject<SourceDocument> sourceDocDAO, List<String> keys, int trainingCount) {
    this.sourceDocDAO = sourceDocDAO;
    this.keys = keys;
    this.trainingCount = trainingCount;
    
    this.classifier = new NaiveBayes();
    this.filter = new Randomize();
  }
  
  public void evaluate (Results results) {
    try {
      buildAttributeInfo();
      loadTrainingInstances();
      buildClassifier();
      testNextInstance(results);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  
  /**
   * sets the file to use for training
   */
  private void buildAttributeInfo() throws Exception {
    dictionary = new Dictionary(INITIAL_DICTIONARY_SIZE);
    classSet = new ClassSet();
    
    for (int k = 0; k < trainingCount; k++) {
      SourceDocument d = sourceDocDAO.getByPrimary(keys.get(k));

      String originName = d.getOriginName();
      String companyName = originName.substring(0, 3);
      classSet.add(companyName);
      seen.add(companyName);
      
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
    
    // Calculate idf's for all words in the dictionary
    idfs = new double[dictionary.size() + 1];
    for (int i = 0; i < dictionary.size(); i++) {
      idfs[i] = Math.log(documentCount / wordDocCounts[i]);
    }
    idfs[dictionary.size()] = 1.0;
        
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
  
  
  private void testNextInstance(Results results) throws Exception {
    if (trainingCount == 0) {
      System.out.println("First instance");
    } else {
      SourceDocument doc = sourceDocDAO.getByPrimary(keys.get(trainingCount + 1));

      String originName = doc.getOriginName();
      String companyName = originName.substring(0, 3);
      if (!seen.contains(companyName)) {
        System.out.println("First instance for " + companyName);
      } else {
        // Make separate little test set so that message
        // does not get added to string attribute in mData.
        Instances testSet = instanceSet.stringFreeStructure();

        // Make message into test instance.
        Instance testInst = buildInstance(testSet, doc, false);
        double predicted = classifier.classifyInstance(testInst);
        String predictedName = classSet.getValue((int)predicted);
        if (companyName.equals(predictedName)) {
          System.out.println("Successfuly prediction of " + companyName);
          results.success++;
        } else {
          System.out.println("Predicted " + predictedName + " when was " + companyName + ": " + doc.getOriginName());
        }
        results.total++;
      }
    }
  }
  
  
  private Instance buildInstance(Instances dataSet, SourceDocument doc, boolean includeClass) {
    // Count word use in this instance
    ISourceDocumentContents dc = doc.getContents();
    int[] wordCounts = new int[dictionary.size()];
    
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
          int i = dictionary.query(word);
          if (i != -1) {
            wordCounts[i]++;
          }
        }
        break;
      }
    }

    // Create new instance and set word counts
    //Arrays.fill(idfs, 1.0);
    //Instance inst = new PostAdjustedInstance(new SparseInstance(dictionary.size() + 1), idfs);
    Instance inst = new SparseInstance(dictionary.size() + 1);
    for (int i = 0; i < wordCounts.length; i++) {
      if (wordCounts[i] > 0) {
        inst.setValue(i, wordCounts[i] * idfs[i]);
      }
    }

    // Link this instance to the data set
    inst.setDataset(instanceSet);
    
    // Add the class
    if (includeClass) {
      addClass(inst, doc);
    }
    return inst;
  }
  
  
  private void addClass (Instance inst, SourceDocument doc) {
    String originName = doc.getOriginName();
    String companyName = originName.substring(0, 3);
    inst.setClassValue(classSet.indexOf(companyName));
  }

  
  private void loadTrainingInstances () {
    // Create instances
    for (int i = 0; i < trainingCount; i++) {
      SourceDocument d = sourceDocDAO.getByPrimary(keys.get(i));
      Instance inst = buildInstance(instanceSet, d, true);
      // And finally add this instance to the training data set
      instanceSet.add(inst);
    }
  }
  

  /**
   * runs 10fold CV over the training file
   */
  private void buildClassifier() {
    try {
      // run filter
      filter.setInputFormat(instanceSet);
      filtered = Filter.useFilter(instanceSet, filter);

      // train classifier on complete file for tree
      classifier.buildClassifier(filtered);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

}
