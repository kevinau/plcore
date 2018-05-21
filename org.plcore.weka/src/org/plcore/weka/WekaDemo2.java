package org.plcore.weka;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Randomize;

//@Component (immediate = true)
public class WekaDemo2 {
  
  /** the classifier used internally */
  private Classifier classifier = null;

  /** the filter to use */
  private Filter filter = null;

  /** the training file */
  private String trainingFile = null;

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
  public void setTraining(String name) throws FileNotFoundException {
    try {
      trainingFile = name;
      training = new Instances(new BufferedReader(new FileReader(trainingFile)));
      training.setClassIndex(training.numAttributes() - 1);
    } catch (FileNotFoundException ex) {
      throw ex;
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
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

    result.append(classifier.toString() + "\n");
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
        "-U",
    };
    //String filter = "weka.filters.unsupervised.instance.Randomize";
    String[] filterOptions = {
    };
    String dataset = "C:/Users/Kevin/code/mullet4/org.plcore.weka/iris.arff";

    // run
    classifier = new J48();
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
    setTraining(dataset);
    execute();
    System.out.println(toString());
  }
  
}
