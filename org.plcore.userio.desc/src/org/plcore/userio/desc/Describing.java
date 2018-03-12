package org.plcore.userio.desc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that marks a field as being part of an entity's description.
 * 
 * See DescriptionFactory for how this annotation can be used to build an entity's 
 * description.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Describing {

}
