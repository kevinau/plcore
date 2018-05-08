package org.apache.felix.fileinstall.internal;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.plcore.osgi.DynamicConfigurer1;

//@Component(immediate = true)
public class TestDAOConfig {

  @Reference
  private DynamicConfigurer1 dynamicConfigurer;
  
  @Activate
  private void activate(BundleContext bundleContext) {
    Dictionary<String, Object> props = new Hashtable<>();
    props.put("name", "ASXSector");
    props.put("class", "org.pennyledger.data.asx200.ASXSector");
    try {
      System.out.println("............ about to config DataAccessObject");
      dynamicConfigurer.setConfig("org.plcore.dao.berkeley.DataAccessObject", "ASXSector", props);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Deactivate
  private void deactivate() {
  }
  
}
