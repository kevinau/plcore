package org.plcore.inbox;

import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Pattern;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.plcore.dao.IDataAccessObject;
import org.plcore.nio.DirectoryWatcher;
import org.plcore.nio.DirectoryWatcher.EventKind;
import org.plcore.osgi.ComponentConfiguration;
import org.plcore.osgi.Configurable;
import org.plcore.util.MD5DigestFactory;
import org.plcore.value.ExistingDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service=InboxWatchService.class, configurationPolicy=ConfigurationPolicy.REQUIRE, immediate=true)
public class InboxWatchService {

  private final Logger logger = LoggerFactory.getLogger(InboxWatchService.class);

  private final MD5DigestFactory digestFactory = new MD5DigestFactory();
  
  @Reference(name = "processor")
  private IFileProcessor fileProcessor;
  
  @Reference(target = "(name=InboxSeen)")
  private IDataAccessObject<InboxSeen> dao;

  @Configurable(name = "watch", required = true)
  private ExistingDirectory watchDir;

  @Configurable(name = "matching")
  private final Pattern pattern = Pattern.compile(".");
  
  private DirectoryWatcher watchService;
  
  /**
   * Before the addition of a seen object, are there any with this
   * digest.  If not, this is the first digest for the file
   * and this file/digest should be processed.
   */
  private String firstDigestForFile (String digest) {
    InboxSeen seen = dao.getByIndex("digest", digest);
    if (seen == null) {
      return digest;
    } else {
      return null;
    }
  }
  
  private void conditionallyProcess(Path path, String digest) {
    if (digest != null) {
      fileProcessor.process(path, digest);
    };
  }
  
  
  private void conditionallyUnprocess(Path path, String digest) {
    InboxSeen seen = dao.getByIndex("digest", digest);
    if (seen == null) {
      fileProcessor.unprocess(path, digest);
    };
  }
  
  
  @Activate
  protected void activate (ComponentContext context) throws IOException {
    ComponentConfiguration.load(this, context);
    
    logger.info("{}: watching for new/changed files", watchDir);
    
    // Create a processor to import the documents found in the download directory
    DirectoryWatcher.IProcessor watchProcessor = new DirectoryWatcher.IProcessor() {
      @Override
      public void process(Path path, EventKind kind) throws IOException {
        logger.info("{}: Found {} {}", watchDir, kind, watchDir.relativize(path));
        String fileName = watchDir.relativize(path).toString();
        switch (kind) {
        case EXISTING :
        case CREATE :
        case MODIFY :
          String digest = digestFactory.getFileDigest(path).toString();

          InboxSeen seen = dao.getByPrimary(fileName);
          if (seen != null) {
            if (digest.equals(seen.digest)) {
              // No change in file.  Already processed.  No action required.
              //logger.info("{}: No change... {} {}, {}", watchDir, kind, watchDir.relativize(path), digest);
            } else {
              // The file has been changed since last processing.
              String firstDigest = firstDigestForFile(digest);
              String priorDigest = seen.digest;
              logger.info("{}: {} {}, {}", watchDir, kind, watchDir.relativize(path), digest);
              conditionallyUnprocess(path, priorDigest);
              conditionallyProcess(path, firstDigest);
              seen.digest = digest;
              dao.update(seen);
            }
          } else {
            // As expected by the ENTRY_CREATE, the file is new and MAY need to be processed.
            String firstDigest = firstDigestForFile(digest);
            seen = new InboxSeen(fileName, digest);
            logger.info("{}: {} {}, {}", watchDir, kind, watchDir.relativize(path), digest);
            conditionallyProcess(path, firstDigest);
            dao.add(seen);
          }
          break;
        case DELETE :
          InboxSeen seen2 = dao.getByPrimary(fileName);
          if (seen2 == null) {
            throw new IOException("No 'seen' record for deletion of: " + path);
          }
          logger.info("{}: {} {}, {}", watchDir, kind, watchDir.relativize(path), seen2.digest);
          conditionallyUnprocess(path, seen2.digest);
          dao.remove(seen2);
          break;
        }
      }
    };
    
    watchService = new DirectoryWatcher(watchDir, pattern, watchProcessor);
    watchService.registerDirectory(watchDir);
    
    // Start watching the directory in a separate thread.
    new Thread() {
      @Override
      public void run() {
        watchService.start();
      }
    }.start();
    
    // Fire off the processor for existing files
    watchService.queueExistingFiles();
  }
  
  
  @Deactivate
  protected void deactivate () {
    watchService.close();
    logger.info("{}: no longer watching for new/changed files", watchDir);
  }
  
}
