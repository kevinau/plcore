package org.plcore.dao.berkeley;

import org.plcore.util.MimeType;
import org.plcore.util.MimeTypeFactory;

import com.sleepycat.persist.model.Persistent;
import com.sleepycat.persist.model.PersistentProxy;


@Persistent(proxyFor = MimeType.class)
class MimeTypeProxy implements PersistentProxy<MimeType> {

  private static final MimeTypeFactory mimeTypeFactory = new MimeTypeFactory();
  
  String mimeTypeName;

  private MimeTypeProxy() {
  }


  @Override
  public void initializeProxy(MimeType object) {
    mimeTypeName = object.getMimeType();
  }


  @Override
  public MimeType convertProxy() {
    return mimeTypeFactory.getMimeType(mimeTypeName);
  }

}
