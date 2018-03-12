package org.plcore.userio;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface CodeSource {

  /**
   * The name of a ICodeSource field, or a method that returns an ICodeSource.
   * This annotation only makes sense when applied to ICode type IO fields. The
   * ICodeSource returns the set of values for the ICode field.
   */
  Class<?> value() default void.class;

}
