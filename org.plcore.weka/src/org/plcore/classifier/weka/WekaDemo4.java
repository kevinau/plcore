package org.plcore.classifier.weka;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.classifier.ClassSet;
import org.plcore.classifier.Dictionary;
import org.plcore.dao.IDataAccessObject;
import org.plcore.srcdoc.ISegment;
import org.plcore.srcdoc.ISourceDocumentContents;
import org.plcore.srcdoc.SourceDocument;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Randomize;

//@Component (immediate = true)
public class WekaDemo4 {
  
  private static class Results {
    int success;
    int total;
    
    @Override
    public String toString() {
      double x = (double)success * 100 / total;
      return success + " out of " + total + ", " + (int)x + "%";
    }
  }
  
  @Reference(target = "(name=SourceDocument)")
  private IDataAccessObject<SourceDocument> sourceDocDAO;

  /** the classifier used internally */
  private NaiveBayesUpdateable classifier = null;

  /** the filter to use */
  private Filter filter = null;

  /** the training file */
  private String trainingFile = null;

  private int documentCount = 0;

  private int[] wordDocCounts = new int[2000];

  private Dictionary dictionary;

  private ClassSet classSet;
  
  private double[] idfs;
  
  /** the training instances */
  private Instances instanceSet = null;

  private Instances filtered = null;
  
  /** for evaluating the classifier */
  private Evaluation evaluation = null;

  
  @Activate
  private void activate (ComponentContext context) throws Exception {
    //String classifierx = "weka.classifiers.trees.J48";
    String[] classifierOptions = {
//        "-U",
    };
    //String filter = "weka.filters.unsupervised.instance.Randomize";
    String[] filterOptions = {
    };

    // run
    classifier = new NaiveBayesUpdateable();
    if (classifier instanceof OptionHandler) {
      ((OptionHandler)classifier).setOptions(classifierOptions);
    }

    filter = new Randomize();
    if (filter instanceof OptionHandler) {
      ((OptionHandler)filter).setOptions(filterOptions);
    }

    buildAttributeInfo();
    this.buildInitialClassifier();
    
    List<String> keys = getOrderedKeys();
    int n = 0;
    List<String> seen = new ArrayList<>();
    Results results = new Results();
    for (String key : keys) {
      testAndLoadInstance(n, key, seen, results);
      n++;
    }
    System.out.println(results);
    
//    loadInstances1();
//    buildInitialClassifier();
//    evaluate();
//    System.out.println(toString());
//    addMoreInstances();
//    evaluate();
//    System.out.println(toString());
  }

  
  /**
   * sets the file to use for training
   */
  public void buildAttributeInfo() throws FileNotFoundException {
    dictionary = new Dictionary(500);
    classSet = new ClassSet();
    
    sourceDocDAO.getAll(d -> {
      String originName = d.getOriginName();
      String companyName = originName.substring(0, 3);
      classSet.add(companyName);
      
      ISourceDocumentContents dc = d.getContents();
      boolean[] wordOccurs = new boolean[2000];

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
    });
    //wordDocCounts = dictionary.clean(wordDocCounts, documentCount);
    
    // Calculate idf's for all words in the dictionary
    idfs = new double[dictionary.size()];
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
  
  
  private void testAndLoadInstance(int n, String key, List<String> seen, Results results) throws Exception {
    SourceDocument doc = sourceDocDAO.getByPrimary(key.substring(11));
    Instance inst;
    
    if (n == 0) {
      inst = buildInstance(instanceSet, doc, true);
      System.out.println("First instance");
    } else {
      String originName = doc.getOriginName();
      String companyName = originName.substring(0, 3);
      if (!seen.contains(companyName)) {
        seen.add(companyName);
        inst = buildInstance(instanceSet, doc, true);
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
          System.out.println("Successfuly prediction");
          results.success++;
        } else {
          System.out.println("Predicted " + predictedName + " when was " + companyName + ": " + doc.getOriginName());
        }
        results.total++;
        inst = buildInstance(instanceSet, doc, true);
      }
    }
    classifier.updateClassifier(inst);
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
    SparseInstance inst = new SparseInstance(dictionary.size() + 1);
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

  
  private void loadInstances1 () {
    int[] count = new int[1];
    
    // Create instances
    sourceDocDAO.getAll(d -> {
      if (count[0] < 100) {
        Instance inst = buildInstance(instanceSet, d, true);
        // And finally add this instance to the training data set
        instanceSet.add(inst);
      }
      count[0]++;
    });
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
    for (String k : keys) {
      System.out.println(k);
    }
    return keys;
  }

  
  private void addMoreInstances() {
    int[] count = new int[1];
    
    // Create instances
    sourceDocDAO.getAll(d -> {
      if (count[0] >= 100) {
        Instance inst = buildInstance(instanceSet, d, true);
        // And finally add this instance to the training data set
        try {
          classifier.updateClassifier(inst);
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        }
      }
      count[0]++;
    });
  }
  

  /**
   * runs 10fold CV over the training file
   */
  public void buildInitialClassifier() {
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

  
  /**
   * runs 10fold CV over the training file
   */
  public void evaluate() {
    try {
      // 10fold CV with seed=1
      evaluation = new Evaluation(filtered);
      evaluation.crossValidateModel(classifier, filtered, 10, instanceSet.getRandomNumberGenerator(1));
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  /**
   * outputs some data about the classifier
   */
  @Override
  public String toString() {
    StringBuffer result;

    result = new StringBuffer();
    result.append("Weka - Demo\n===========\n\n");

    result.append("Classifier...: " + Utils.toCommandLine(classifier) + "\n");
    if (filter instanceof OptionHandler) {
      result.append("Filter.......: " + filter.getClass().getName() + " "
        + Utils.joinOptions(((OptionHandler) filter).getOptions()) + "\n");
    } else {
      result.append("Filter.......: " + filter.getClass().getName() + "\n");
    }
    result.append("Training file: " + trainingFile + "\n");
    result.append("\n");

    //result.append(classifier.toString() + "\n");
    result.append(evaluation.toSummaryString() + "\n");
//    try {
//      result.append(evaluation.toMatrixString() + "\n");
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//    try {
//      result.append(evaluation.toClassDetailsString() + "\n");
//    } catch (Exception e) {
//      e.printStackTrace();
//    }

    return result.toString();
  }

  
}
