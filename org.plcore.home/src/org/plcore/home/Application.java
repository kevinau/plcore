package org.plcore.home;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.plcore.osgi.Configurable;
import org.plcore.osgi.ConfigurationLoader;


@Component(immediate = true, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class Application implements IApplication {

  @Reference 
  private ConfigurationLoader configLoader;
  
  @Configurable
  private String id = "pennyledger";
  
  @Configurable
  private Path parentDir = Paths.get(System.getProperty("user.home"));

  @Configurable
  private Path baseDir = null;
  

  @Activate
  private void activate(ComponentContext componentContext) {
    configLoader.load(this, componentContext);
    if (baseDir == null) {
      baseDir = parentDir.resolve(id);
    }
  }

  
  @Deactivate
  private void deactivate() {
  }
  
  
  @Override
  public String getId() {
    return id;
  }

  @Override
  public Path getBaseDir() {
    return baseDir;
  }
  
}
