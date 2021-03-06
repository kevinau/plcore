package org.plcore.dao.berkeley;

import java.util.Collection;

import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.Descriptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.plcore.dao.IDataAccessObject;


@Component(property = { 
    CommandProcessor.COMMAND_SCOPE + "=db",
    CommandProcessor.COMMAND_FUNCTION + "=show" },
    service = ShowCommand.class)
public class ShowCommand {

  private BundleContext bundleContext;
  
  @Activate
  private void activate(BundleContext bundleContext) {
    this.bundleContext = bundleContext;
  }


  @SuppressWarnings("rawtypes")
  @Descriptor ("List all table names")
  public void show() throws InvalidSyntaxException {
    Collection<ServiceReference<IDataAccessObject>> serviceRefs = bundleContext.getServiceReferences(IDataAccessObject.class, null);
    int count = 0;
    for (ServiceReference<IDataAccessObject> serviceRef : serviceRefs) {
      String tableName = (String)serviceRef.getProperty("name");
      System.out.println(tableName);
      count++;
    }
    if (count == 0) {
      System.out.println("No tables");
    } else {
      System.out.println("- " + count + " table" + (count == 1 ? "" : "s"));
    }
  }
  
  
  @SuppressWarnings("rawtypes")
  @Descriptor ("List all records of the named table")
  public void show(String name) throws InvalidSyntaxException {
    // Ignore case
    String name1 = name.toLowerCase();
    
    // Find correct name
    Collection<ServiceReference<IDataAccessObject>> serviceRefs = bundleContext.getServiceReferences(IDataAccessObject.class, null);
    String correctTableName = "";
    int n = 0;
    for (ServiceReference<IDataAccessObject> serviceRef : serviceRefs) {
      String tableName = (String)serviceRef.getProperty("name");
      if (tableName.toLowerCase().contains(name1)) {
        correctTableName = tableName;
        n++;
      }
    }
    switch (n) {
    case 0 :
      System.out.println("No table named: " + name);
      break;
    case 1 :
      serviceRefs = bundleContext.getServiceReferences(IDataAccessObject.class, "(name=" + correctTableName + ")");
      if (serviceRefs.size() == 0) {
        throw new IllegalArgumentException("No table named '" + correctTableName + "' was found");
      }
      ServiceReference<IDataAccessObject> serviceRef = serviceRefs.iterator().next();
      IDataAccessObject<?> dao = bundleContext.getService(serviceRef);
    
      int[] count = new int[1];
      dao.getAll(r -> {
        System.out.println(r.toString());
        count[0]++;
      });
      if (count[0] == 0) {
        System.out.println("No objects");
      } else {
        System.out.println("- " + count[0] + " object" + (count[0] == 1 ? "" : "s") + " in " + correctTableName);
      }
      break;
    default :
      System.out.println("More than one table named: " + name);
      break;
    }
  }
  
}