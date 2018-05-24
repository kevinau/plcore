package org.plcore.classifier.weka;

import java.util.Arrays;

public class IdfArray {

  private double[] idfs;
  
  public IdfArray (int n, int[] wordDocCounts, int docCount) {
    idfs = new double[n];
    System.out.println("idfs  ...... " + n);
    for (int i = 0; i < n; i++) {
      idfs[i] = Math.log(docCount / wordDocCounts[i]);
      System.out.println(i + ":  ...... " + idfs[i]);
    }

  }
  
  
  public double[] values() {
    return idfs;
  }
  
  public double adjust (int i, double from) {
    return from * idfs[i];
  }
  
}
