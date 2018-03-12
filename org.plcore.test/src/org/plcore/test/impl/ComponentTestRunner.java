package org.plcore.test.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.startlevel.FrameworkStartLevel;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.plcore.test.IResultSet;
import org.plcore.test.ITestCase;
import org.plcore.test.ResultSet;


@Component(immediate = true)
public class ComponentTestRunner {

  private BundleContext bundleContext;
  
  private List<ITestCase> testCases = new CopyOnWriteArrayList<>();
  
  private Timer timer = null;
  
  private static void pause() {
    try {
      Thread.sleep(1000L);
    } catch (InterruptedException ex) {
      // Do nothing
    }
  }
  
  
  private class RunAllTask extends TimerTask {

    @Override
    public void run() {
      if (timer != null) {
        timer.cancel();
      }
      
      IResultSet resultSet = new ResultSet();
      
      for (ITestCase testCase : testCases) {
        ComponentTest.test(testCase, resultSet);
      }
      resultSet.dump(0);
      
      // Now shutodwn OSGi
      pause();
      System.out.println("Shutting down...");
      try {
        Bundle systemBundle = bundleContext.getBundle(0);
        systemBundle.stop();
      } catch (BundleException ex) {
        // Do nothing
      }
    }
    
  };

    
  @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  public void addTestCase(ITestCase testCase) {
    if (timer != null) {
      timer.cancel();
    }
    
    testCases.add(testCase);

    timer = new Timer();
    timer.schedule(new RunAllTask(), 2000L);
  }
  
  
  public void removeTestCase(ITestCase testCase) {
    // Do nothing
  }
  
  
  @Activate
  public void activate (BundleContext bundleContext) {
    this.bundleContext = bundleContext;
    timer = null;
  }
  
}
