package org.plcore.dao.berkeley;

import org.plcore.math.Decimal;

import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;


@Persistent(proxyFor = Decimal.class)
class DecimalProxy implements PersistentProxy<Decimal> {

  String stringValue;

  private DecimalProxy() {
  }


  @Override
  public void initializeProxy(Decimal object) {
    stringValue = object.toString();
  }


  @Override
  public Decimal convertProxy() {
    return new Decimal(stringValue);
  }

}
