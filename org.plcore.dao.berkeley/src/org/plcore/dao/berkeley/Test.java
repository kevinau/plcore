package org.plcore.dao.berkeley;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.function.Function;
import java.util.function.Supplier;

import org.plcore.entity.IEntity;
import org.plcore.osgi.DynamicConfigurer1;

public class Test<T> {

  protected Supplier<DynamicConfigurer1> dynamicConfigurer2;
  protected Function<T, String> configName2;
  protected Function<T, Dictionary<String, Object>> newProperties2;
  
  public Test(Supplier<DynamicConfigurer1> dynamicConfigurer2, Function<T, String> configName2, Function<T, Dictionary<String, Object>> newProperties2) {
    this.dynamicConfigurer2 = dynamicConfigurer2;
    this.configName2 = configName2;
    this.newProperties2 = newProperties2;
  }
  
  public static class Entity implements IEntity {
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
