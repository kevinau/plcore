package org.plcore.inbox;

import java.nio.file.Path;

public interface IFileProcessor {

  public void process(Path path, String digest);
  
  public default void unprocess(Path path, String digest) {
  };
  
}
