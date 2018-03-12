package org.plcore.test.impl;

public class ConsoleRunListener { //extends RunListener {

//  private ITestCase testCase;
//  
//  private PrintStream writer = System.out;
//  //private PrintStream error = new PrintStream(System.err, true);
//
//  ConsoleRunListener (ITestCase testCase) {
//    this.testCase = testCase;
//  }
//  
//  @Override
//  public void testRunFinished(Result result) {
//    Class<?> klass = testCase.getClass();
//    writer.println(klass.getSimpleName() + " (" + klass.getPackage() + ")");
//    printFailures(result);
//    printFooter(result);
//  }
//
//  protected void printFailures(Result result) {
//    List<Failure> failures = result.getFailures();
//    if (failures.isEmpty()) {
//      return;
//    }
//    if (failures.size() == 1) {
//      writer.println("There was " + failures.size() + " failure:");
//    } else {
//      writer.println("There were " + failures.size() + " failures:");
//    }
//    int i = 1;
//    for (Failure each : failures) {
//      printFailure(each, "" + i++);
//    }
//  }
//
//  protected void printFailure(Failure each, String prefix) {
//    Throwable t = each.getException();
//    while (t != null) {
//      writer.println("       " + t.toString());
//      for (StackTraceElement te : t.getStackTrace()) {
//        String className = te.getClassName();
//        if (className.startsWith("org.junit.Assert")) {
//          continue;
//        }
//        if (className.startsWith("sun.reflect.")) {
//          break;
//        }
//        writer.println("       at " + te.getClassName() + "." + te.getMethodName() + "(" + te.getFileName() + ":" + te.getLineNumber() + ")"); 
//      }
//      t = t.getCause();
//      if (t != null) {
//        writer.println("    caused by:");
//      }
//    }
//  }
//
//  protected void printFooter(Result result) {
//    int runCount = result.getRunCount();
//    int ignored = result.getIgnoreCount();
//    int failures = result.getFailureCount();
//    
//    if (result.wasSuccessful()) {
//      writer.println("OK (" + runCount + " test" + (runCount == 1 ? "" : "s") + ").");
//    } else {
//      writer.flush();
//      try {
//        // Sleep 5 milli seconds.  This is required to allow std out to flush properly.
//        Thread.sleep(5);
//      } catch (InterruptedException ex) {
//        // Do nothing
//      }
//      PrintStream error = System.err;
//      error.print(failures + " failure" + (failures == 1 ? "" : "s") + " out of ");
//      error.println(runCount + " test" + (runCount == 1 ? "" : "s") + ".");
//      error.flush();
//      try {
//        // Sleep 5 milli seconds.  This is required to allow std error to flush properly.
//        Thread.sleep(5);
//      } catch (InterruptedException ex) {
//        // Do nothing
//      }
//    }
//    if (ignored > 0) {
//      if (runCount > 0) {
//        writer.print("In addition, ");
//      }
//      writer.println(ignored + " test" + (ignored == 1 ? " was" : "s were") + " ignored.");
//    }
//    writer.println();
//  }

}
