package org.plcore.dao.berkeley;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Reference;
import org.plcore.entity.IEntity;
import org.plcore.osgi.DynamicConfigurer1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test2 extends AbstractComponentConfiguration<IEntity> {

  private static final String CONFIG_NAME = DataAccessObjectConfiguration.class.getName();
  
  private final Logger logger = LoggerFactory.getLogger(DataAccessObjectConfiguration.class);
  
  @Reference
  private DynamicConfigurer1 dynamicConfigurer;
  
  private boolean activated = false;
  
  private List<IEntity> deferred = new LinkedList<>();
  
  
  public Test2(Supplier<DynamicConfigurer1> dynamicConfigurer2, Function<IEntity, String> configName2, Function<IEntity, Dictionary<String, Object>> newProperties2) {
    super(() -> dynamicConfigurer2, configName2, newProperties2);
  }


  public static void main(String[] args) {
    IEntity dao = new Entity();
    
    DynamicConfigurer1 someConfigurer = new DynamicConfigurer1();
    System.out.println(someConfigurer);
    Test<IEntity> test = new Test<IEntity>(() -> someConfigurer, c -> c.getClass().getSimpleName(), c -> {
      Hashtable<String, Object> props = new Hashtable<>();
      String name = c.getClass().getSimpleName();
      props.put("name", name);
    
      String className = c.getClass().getName();
      if (className.startsWith("org.plcore.")) {
        props.put("store.target", "(name=CoreDataStore)");
      }
      props.put("class", className);
      return props;
    });
    DynamicConfigurer1 s = test.dynamicConfigurer2.get();
    System.out.println(s);
    String s2 = test.configName2.apply(dao);
    System.out.println(s2);
    Dictionary<String, Object> p2 = test.newProperties2.apply(dao);
    System.out.println(p2.size());
    System.out.println(p2);
  }

}
