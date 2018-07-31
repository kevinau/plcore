package org.plcore.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;


public class SimpleCache<T> {

  private static class CacheEntry<T> {
    private final long entryTime;
    private final T value;
    
    private CacheEntry (T value) {
      this.entryTime = System.currentTimeMillis();
      this.value = value;
    }
  }
  
    
  private final Map<String, CacheEntry<T>> map = new HashMap<>();
  
  private Timer commitTimer = new Timer("SimpleCache");
  
  private TimerTask commitTask = null;
  
  private static final long CLEANUP_INTERVAL = 5000L;
  
  
  public T get (String key, Supplier<T> ifNotFound) {
    synchronized(map) {
      CacheEntry<T> cacheEntry = map.get(key);
      if (cacheEntry == null) {
        T value = ifNotFound.get();
        map.put(key, new CacheEntry<T>(value));
        return value;
      } else {
        return cacheEntry.value;
      }
    }
  }
  
  
  public T get (String key) {
    synchronized(map) {
      CacheEntry<T> cacheEntry = map.get(key);
      if (cacheEntry == null) {
        return null;
      } else {
        return cacheEntry.value;
      }
    }
  }
  
  
  public T put (String key, T value) {
    synchronized(map) {
      CacheEntry<T> oldEntry = map.get(key);
      map.put(key, new CacheEntry<T>(value));
      stopScheduledCleanup();
      scheduleCleanup();
      if (oldEntry == null) {
        return null;
      } else {
        if (oldEntry.value instanceof AutoCloseable) {
          try {
            ((AutoCloseable)oldEntry.value).close();
          } catch (Exception ex) {
            throw new RuntimeException(ex);
          }
        }
        return oldEntry.value;
      }
    }
  }
  
  
  public T remove (String key) {
    synchronized(map) {
      CacheEntry<T> oldEntry = map.get(key);
      map.remove(key);
      if (oldEntry == null) {
        return null;
      } else {
        if (oldEntry.value instanceof AutoCloseable) {
          try {
            ((AutoCloseable)oldEntry.value).close();
          } catch (Exception ex) {
            throw new RuntimeException(ex);
          }
        }
        return oldEntry.value;
      }
    }
  }

  
  private void stopScheduledCleanup() {
    System.out.println("Stop scheduled cleanup");
    commitTimer.cancel();
  }

  
  private void scheduleCleanup () {
    commitTask = new TimerTask() {
      @Override
      public void run() {
        cleanup();
      }
    };
    commitTimer = new Timer("SimpleCache");
    commitTimer.schedule(commitTask, CLEANUP_INTERVAL);
  }

  
  private void cleanup () {
    System.out.println("Cleaning up cache");
    long expireTime = System.currentTimeMillis() - 10000;
    ArrayList<String> expired = null;
    int currentCount = 0;
    
    synchronized (map) {
      expired = new ArrayList<>((map.size() / 2) + 1);
      
      for (Map.Entry<String, CacheEntry<T>> entry : map.entrySet()) {
        if (entry.getValue().entryTime < expireTime) {
          expired.add(entry.getKey());
        } else {
          currentCount++;
        }
      }
    }
    
    for (String key : expired) {
      System.out.println("removing key..." + key);
      remove(key);
      Thread.yield();
    }

    System.out.println("Current count " + currentCount);
    stopScheduledCleanup();
    if (currentCount > 0) {
      // Schedule another cleanup
      scheduleCleanup();
//      commitTimer.schedule(commitTask, CLEANUP_INTERVAL);
    }
  }
  

  public void close() {
    stopScheduledCleanup();
  }
    

//  public static void main (String[] args) throws Exception {
//    SimpleCache<String> cache = new SimpleCache<>();
//    String v = cache.get("AAAA");
//    System.out.println("1......." + v);
//    cache.put("AAAA","aaaa");
//    v = cache.get("AAAA");
//    System.out.println("2......." + v);
//    v = cache.put("AAAA","dddd");
//    System.out.println("2a......" + v);
//    v = cache.put("AAAA","eeee");
//    System.out.println("2b......" + v);
//    Thread.sleep(15000);
//    cache.get("AAAA");
//    v = cache.get("AAAA");
//    System.out.println("3......." + v);
//    
//    String v2 = cache.get("BBBB", () -> "bbbb");
//    System.out.println("4......." + v2);
//    v2 = cache.get("BBBB", () -> "bbbb");
//    System.out.println("5......." + v2);
//    v2 = cache.remove("BBBB");
//    System.out.println("6......." + v2);
//    v2 = cache.remove("BBBB");
//    System.out.println("7......." + v2);
//    
//    cache.close();
//  }
}
