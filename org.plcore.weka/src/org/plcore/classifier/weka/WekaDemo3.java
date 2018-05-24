package org.plcore.classifier.weka;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.dao.IDataAccessObject;
import org.plcore.srcdoc.ISegment;
import org.plcore.srcdoc.ISourceDocumentContents;
import org.plcore.srcdoc.SourceDocument;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.SparseInstance;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Randomize;

//@Component (immediate = true)
public class WekaDemo3 {
  
  @Reference(target = "(name=SourceDocument)")
  private IDataAccessObject<SourceDocument> sourceDocDAO;

  /** the classifier used internally */
  private Classifier classifier = null;

  /** the filter to use */
  private Filter filter = null;

  /** the training file */
  private String trainingFile = null;

  private int documentCount = 0;

  private int[] wordDocCounts = new int[2000];

  /** the training instances */
  private Instances training = null;

  /** for evaluating the classifier */
  private Evaluation evaluation = null;

  
//  /**
//   * sets the classifier to use
//   * 
//   * @param name the classname of the classifier
//   * @param options the options for the classifier
//   */
//  public void setClassifier(String name, String[] options) {
//    try {
//      classifier = AbstractClassifier.forName(name, options);
//    } catch (Exception ex) {
//      throw new RuntimeException(ex);
//    }
//  }

//  /**
//   * sets the filter to use
//   * 
//   * @param name the classname of the filter
//   * @param options the options for the filter
//   */
//  public void setFilter(String name, String[] options) {
//    try {
//      filter = (Filter)Class.forName(name).newInstance();
//      if (filter instanceof OptionHandler) {
//        ((OptionHandler)filter).setOptions(options);
//      }
//    } catch (Exception ex) {
//      throw new RuntimeException(ex);
//    }
//  }

  /**
   * sets the file to use for training
   */
  public void buildTrainingInstances() throws FileNotFoundException {
    ClassSet classSet = new ClassSet();
    Dictionary dictionary = new Dictionary(512);
    
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
    double[] idfs = new double[dictionary.size()];
    for (int i = 0; i < dictionary.size(); i++) {
      idfs[i] = Math.log(documentCount / (1.0 + wordDocCounts[i]));
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
    training = new Instances("Training data", attributeList, documentCount);
    training.setClassIndex(training.numAttributes() - 1);

    // Create instances
    sourceDocDAO.getAll(d -> {
      // Count word use in this instance
      ISourceDocumentContents dc = d.getContents();
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
      inst.setDataset(training);
      
      // Add the class
      String originName = d.getOriginName();
      String companyName = originName.substring(0, 3);
      inst.setClassValue(classSet.indexOf(companyName));

      // And finally add this instance to the training data set
      training.add(inst);
    });
  }
  

  /**
   * runs 10fold CV over the training file
   */
  public void execute() {
    try {
      // run filter
      filter.setInputFormat(training);
      Instances filtered = Filter.useFilter(training, filter);

      // train classifier on complete file for tree
      classifier.buildClassifier(filtered);

      // 10fold CV with seed=1
      evaluation = new Evaluation(filtered);
      evaluation.crossValidateModel(classifier, filtered, 10, training.getRandomNumberGenerator(1));
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
    try {
      result.append(evaluation.toMatrixString() + "\n");
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      result.append(evaluation.toClassDetailsString() + "\n");
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result.toString();
  }

  /**
   * runs the program, the command line looks like this:<br/>
   * WekaDemo CLASSIFIER classname [options] FILTER classname [options] DATASET
   * filename <br/>
   * e.g., <br/>
   * java -classpath ".:weka.jar" WekaDemo \<br/>
   * CLASSIFIER weka.classifiers.trees.J48 -U \<br/>
   * FILTER weka.filters.unsupervised.instance.Randomize \<br/>
   * DATASET iris.arff<br/>
   */
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
    classifier = new NaiveBayes();
    if (classifier instanceof OptionHandler) {
      ((OptionHandler)classifier).setOptions(classifierOptions);
    }

    filter = new Randomize();
    if (filter instanceof OptionHandler) {
      ((OptionHandler)filter).setOptions(filterOptions);
    }

    
    //classifier.setOptions(classifierOptions);

    //setClassifier(classifier, classifierOptions);
    //setFilter(filter, filterOptions);
    buildTrainingInstances();
    execute();
    System.out.println(toString());
  }
  
}
