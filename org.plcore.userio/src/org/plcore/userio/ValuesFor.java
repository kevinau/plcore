/*******************************************************************************
 * Copyright  2012 Kevin Holloway (kholloway@geckosoftware.co.uk).
 *
 * Licensed under the EUPL, Version 1.1 only (the "Licence").  You may not use
 * this work except in compliance with the Licence.  You may obtain a copy of
 * the Licence at: http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the Licence for the
 * specific language governing permissions and limitations under the Licence.
 *
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 *******************************************************************************/
package org.plcore.userio;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field or method as providing a set of values for ICode fields.
 * <p>
 * If a field is annotated its type must be List<ICode> or ICode[].
 * <p>
 * If a method is annotated it must take no parameters and return a List<ICode> or ICode[].
 * 
 * @author Kevin Holloway
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ValuesFor {
  
  /**
   * A list of path expressions that identify IO fields that the annotated field or method
   * provides values for.  The path expressions must identify ICode fields.
   */
  String[] value() default {};

}
