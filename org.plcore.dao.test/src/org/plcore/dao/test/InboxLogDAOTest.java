package org.plcore.dao.test;

import java.util.Timer;
import java.util.TimerTask;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.dao.IDataAccessObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true)
public class InboxLogDAOTest {

  private Logger logger = LoggerFactory.getLogger(InboxLogDAOTest.class);
  
  @Reference(target = "(name=InboxLog)")
  private IDataAccessObject<InboxLog> dao;
  
  private void runTest() {
    InboxLog log0 = new InboxLog("somefile.txt", "0011223344556677");

    logger.info("About to add {}", log0);
    InboxLog log1 = dao.add(log0);
    logger.info("Added: {}", log1);
    
    int id = log1.getId();
    logger.info("About to fetch record with id {}", id);
    InboxLog log2 = dao.getById(id);
    logger.info("Fetched: {}", log2);
    
    logger.info("All records");
    dao.getAll(r -> {
      logger.info("   {}", r.toString());
    });

//    logger.info("About to change record with id {}", id);
//    party1.setFormalName("Qantas Airways Australia");
//    logger.info("New record {}", party1);
//    try {
//      party1 = (Party)dao.update(party1);
//    } catch (ConcurrentModificationException ex) {
//      logger.error("Record change failure", ex);
//    }
//    logger.info("Record changed: {}", party1);
    
//    logger.info("About to remove record with id {} and versin {}", id, entityData.getVersionTime());
//    try {
//      dao.remove(entityData);
//    } catch (ConcurrentModificationException ex) {
//      logger.error("Record removal failure", ex);
//    }
//    logger.info("Record removed");
  }

  
  @Activate
  protected void activate() {
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        runTest();
        timer.cancel();
      }
    }, 10 * 1000);
  }

}
