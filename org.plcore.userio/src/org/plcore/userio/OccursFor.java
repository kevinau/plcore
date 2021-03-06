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
 * Marks a field or method that returns "occurs" information for repeating models.
 * The field or method can return one of the following:
 * * A single integer value.  This sets the occurs for the first dimension of the repeating model.  The
 *   minimum and maximum values are set to the same value.
 * * An array of two integers.  This sets the minimum and maximum occurs for the first dimension of the 
 *   repeating model.
 * * An array of (an array of two integers).  This sets the minimum and maximum occurs for all the dimensions
 *   that are in the array.
 *   
 * After the above values are assigned, any remaining dimensions are set to 0 and Integer.MAX_VALUE respectively.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface OccursFor {
  
  String[] value() default {};

}
