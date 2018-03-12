package org.plcore.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {

  /**
   * The description for this method.  If not specified, the method name is changed from
   * camel case to a sentence, and that is used.
   */
  public String description() default "";
  
  /**
   * The name of a field or method that provides an array of parameters for the annotated method.
   */
  public String dataProvider() default "";
  
}