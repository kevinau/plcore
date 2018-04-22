/*******************************************************************************
 * Copyright (C) 2018 Kevin Holloway (kholloway@pennyledger.org)
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.plcore.nio;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.plcore.value.ExistingDirectory;

/** 
 * A wrapper around the Java WatchService that specifically supports
 * watching for file creation.
 * <p>
 * This class is used as follows:
 * <ol>
 * <li>Create an instance of NewFileWatchService.</li>
 * <li>Register those directories you want to watch, using registerDirectory().</li>
 * <li>Start the watcher, using start().</li>
 * <li>If necessary, queue existing files, using queueExistingFiles().</li>
 * </ol>
 * @author Kevin
 *
 */
public class DirectoryWatcher implements AutoCloseable {
  
  public enum EventKind {
    CREATE,
    MODIFY,
    DELETE,
    EXISTING;
  }
  
  private static final int WAIT_TIME = 1000;
  
  private WatchService watchService;
  
  private final Timer timer = new Timer();
  private final List<Path> waitList = new ArrayList<>();
  //private final Path topDir;
  private final Pattern pattern;
  private final IProcessor processor;
  
  private final Map<WatchKey, Path> keys = new HashMap<>();
  
  
  public static interface IProcessor {
    public void process (Path path, EventKind kind) throws IOException;
  }
  
  
  public DirectoryWatcher (Path topDir, Pattern pattern, IProcessor processor) {
    // We obtain the file system of the Path
    FileSystem fs = topDir.getFileSystem ();
    
    // We create the new WatchService using the new try() block
    try {
      watchService = fs.newWatchService();
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
    
    this.pattern = pattern;
    this.processor = processor;
  }
  
  
  public void start () {
    try {    
      // Start the infinite polling loop
      WatchKey key = null;
      while(true) {
        key = watchService.take();

        Path dir = keys.get(key);
        if (dir == null) {
            System.err.println("WatchKey not recognized!!");
            continue;
        }
        
        // Dequeueing events
        Kind<?> kind = null;
        for(WatchEvent<?> watchEvent : key.pollEvents()) {
          // Get the type of the event
          kind = watchEvent.kind();
          if (kind == OVERFLOW) {
            continue; //loop
          } else if (kind == ENTRY_CREATE) {
            // A new file was created.
            // Wait for the file to become stable before invoking the processor.
            Path eventFile = (Path)watchEvent.context();
            queueFile (dir.resolve(eventFile), EventKind.CREATE);
          } else if (kind == ENTRY_MODIFY) {
            // A new file was modified.
            // Wait for the file to become stable before invoking the processor.
            Path eventFile = (Path)watchEvent.context();
            queueFile (dir.resolve(eventFile), EventKind.MODIFY);
          } else if (kind == ENTRY_DELETE) {
            // Deleted files are deleted.  We don't need (and cannot) wait for them to be stable.
            Path eventFile = (Path)watchEvent.context();
            try {
              processor.process(dir.resolve(eventFile), EventKind.DELETE);
            } catch (IOException ex) {
              throw new RuntimeException(ex);
            }
          }
        }
        
        if(!key.reset()) {
          break; //loop
        }
      }
    } catch (InterruptedException ex) {
      // Ignore this exception
    } catch (ClosedWatchServiceException ex) {
      // Ignore this exception
    }
  }


  public void queueExistingFiles () {
    for (Path registeredDir : keys.values()) {
      // The following is required to overcome a bug (feature?) of the Java Path class.
      Path dir2;
      if (registeredDir instanceof ExistingDirectory) {
        dir2 = Paths.get(registeredDir.toString());
      } else {
        dir2 = registeredDir;
      }
      // End bug fix.

      try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir2)) {
        for (Path fileEntry : stream) {
          if (Files.isRegularFile(fileEntry)) {
            queueFile (fileEntry, EventKind.EXISTING);
          }
        }
      } catch (IOException ex) {
        throw new UncheckedIOException(ex);
      }
    }
  }
  
  
  private void queueFile (Path path, EventKind kind) {
    String fileName = path.getFileName().toString();
    Matcher matcher = pattern.matcher(fileName);
    if (matcher.find()) {
      synchronized (waitList) {
        if (!waitList.contains(path)) {
          waitList.add(path);
  
          // Wait for the file to be stable
          long size;
          try {
            size = Files.size(path);
          } catch (IOException e) {
            size = -1;
          }
          timer.schedule(new WaitStableTask(processor, path, kind, size), WAIT_TIME); 
        }
      }
    }
  }

  
  @Override
  public void close () {
    try {
      watchService.close();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  public void registerDirectory (Path path) {
    registerDirectory (path, false);
  }
  
  
  public void registerDirectory (Path path, boolean recursive) {
    try {
      WatchKey key = path.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
      keys.put(key, path);
      
      if (recursive) {
        String[] childNames = path.toFile().list();
        for (String childName : childNames) {
          Path childPath = path.resolve(childName);
          if (Files.isDirectory(childPath) && Files.isReadable(childPath)) {
            registerDirectory (childPath, recursive);
          }
        }
      }
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  
  private class WaitStableTask extends TimerTask {

    private final IProcessor processor;
    private final Path path;
    private final EventKind kind;
    private final long lastLength;
    
    private WaitStableTask (IProcessor processor, Path path, EventKind kind, long lastLength) {
      this.processor = processor;
      this.path = path;
      this.kind = kind;
      this.lastLength = lastLength;
    }
    
    @Override
    public void run() {
      long currLength;
      if (Files.exists(path)) {
        try {
          currLength = Files.size(path);
        } catch (IOException ex) {
          currLength = -1;
        }
      } else {
        currLength = -1;
      }
      if (currLength == lastLength) {
        synchronized (waitList) {
          waitList.remove(path);
        }
        if (currLength == -1) {
          // Do nothing.  The file has gone.
        } else {
          try {
            processor.process(path, kind);
          } catch (IOException ex) {
            throw new RuntimeException(ex);
          }
        }
      } else {
        timer.schedule(new WaitStableTask(processor, path, kind, currLength), WAIT_TIME);
      }
    }
  }
}
