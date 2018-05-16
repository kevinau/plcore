package org.plcore.dao.berkeley;

import java.sql.Timestamp;
import java.time.Instant;

import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;


@Persistent(proxyFor = Timestamp.class)
class TimestampProxy implements PersistentProxy<Timestamp> {

  long seconds;
  int nanos;

  private TimestampProxy() {
  }


  @Override
  public void initializeProxy(Timestamp object) {
    Instant instant = object.toInstant();
    seconds = instant.getEpochSecond();
    nanos = instant.getNano();
  }


  @Override
  public Timestamp convertProxy() {
    Instant instant = Instant.ofEpochSecond(seconds, nanos);
    return Timestamp.from(instant);
  }

}
