package org.plcore.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * An annotation that marks a field that can be searched.
 * <p>
 * Note that the annotated field does not have to uniquely identify an entity.  Rather,
 * it indicates that a search on this field should return one or more entities that are
 * distingushable by their description.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Identifiable {

}
