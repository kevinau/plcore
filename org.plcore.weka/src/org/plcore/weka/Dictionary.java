package org.plcore.weka;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A map of words/phrases and associated index.
 */
public class Dictionary {

  private Map<String, Integer> map = new LinkedHashMap<>(500);
  

  public Set<String> words() {
    return map.keySet();
  }
  
  
  public int get(String key) {
    Integer value = map.get(key);
    if (value == null) {
      return -1;
    } else {
      return value;
    }
  }

  
  public int size() {
    return map.size();
  }

  
  public int[] clean (int[] docCounts) {
    int j = 0;
    int[] compactDocCounts = new int[docCounts.length];
    
    Set<Map.Entry<String, Integer>> set = map.entrySet();
    Iterator<Map.Entry<String, Integer>> itr = set.iterator();
    while (itr.hasNext()) {
      Map.Entry<String, Integer> entry = itr.next();
      int i = entry.getValue();
      if (docCounts[i] == 1) {
        itr.remove();
      } else {
        compactDocCounts[j] = docCounts[i];
        entry.setValue(j);
        j++;
      }
    }
    return Arrays.copyOf(compactDocCounts, j);
  }
  
  
  public void reindex () {
    int i = 0;
    for (Map.Entry<String, Integer> entry : map.entrySet()) {
      entry.setValue(i);
      i++;
    }
  }
  
  
  public String getWord (int wordIndex) {
    // This is not efficient, but it is used for debugging only
    for (Map.Entry<String, Integer> entry : map.entrySet()) {
      if (entry.getValue() == wordIndex) {
        return entry.getKey();
      }
    }
    return "null(" + wordIndex + ")";
  }
 
  
  public int resolve(String word) {
    Integer value = map.get(word);
    if (value == null) {
      value = map.size();
      map.put(word, value);
    }
    return value;
  }

  
  public int query(String word) {
    Integer value = map.get(word);

    if (value == null) {
      return -1;
    } else {
      return value;
    }
  }

  
//  @Deprecated
//  public int getTwoPlusDictionaryWordCounts() {
//    int n = 0;
//    for (int i = 0; i < dictWordCounts.length; i++) {
//      if (dictWordCounts[i] >= 2) {
//        n++;
//      }
//    }
//    return n;
//  }
//
//  
//  @Deprecated
//  public void purgeSingleWords () {
//    if (isReadOnly) {
//      throw new IllegalStateException("Cannot purge single words from a read-only dictionary");
//    }
//    Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
//    while (it.hasNext()) {
//       Map.Entry<String, Integer> entry = it.next();
//       int i = entry.getValue();
//       if (dictWordCounts[i] <= 1) {
//         it.remove();
//       }
//    }
//    int i = 0;
//    for (Map.Entry<String, Integer> entry : map.entrySet()) {
//      entry.setValue(i);
//      i++;
//    }
//  }
//  
//  
//  public int[] getDictionaryWordCounts() {
//    return dictWordCounts;
//  }
//  
//
//  public void updateWordCounts() {
//    if (isReadOnly) {
//      throw new IllegalStateException("Cannot update word counts for a read-only dictionary");
//    }
//    dictWordCounts = Arrays.copyOf(dictWordCounts, map.size());
//    for (int i = 0; i < docWordCounts.size(); i++) {
//      int docCount = docWordCounts.get(i);
//      if (docCount > 0) {
//        dictWordCounts[i]++;
//      }
//    }
//    docWordCounts = new ArrayList<>(map.size());
//    int mapSize = map.size();
//    while (docWordCounts.size() < mapSize) {
//      docWordCounts.add(0);
//    }
//  }

  public void dump() {
    for (Map.Entry<String, Integer> entry : map.entrySet()) {
      System.out.println(entry.getKey() + "||" + entry.getValue());
    }
  }

  public void save(Path file) {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.toFile()))) {
      oos.writeObject(this);
    } catch (IOException ex) {
      throw new RuntimeException();
    }
  }

  public static Dictionary load(Path file) {
    Dictionary dictionary;
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.toFile()))) {
      dictionary = (Dictionary) ois.readObject();
    } catch (ClassNotFoundException | IOException ex) {
      throw new RuntimeException(ex);
    }
    return dictionary;
  }

}
