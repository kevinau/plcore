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
package org.plcore.userio.plan.impl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.plcore.userio.Occurs;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface MultipleOccurs {

  /**
   * The minimum and maximum number of occurrences for multiple dimensions of the repeating field.
   * 
   * If one value is provided it is assigned to the minimum and maximum number of occurrences.  This
   * effectively says the repeating field has exactly this number of occurences.
   * 
   * If two values are provided, the first value is assigned to the minimum number of occurrences and
   * the second value is assigned to the maximum number of occurrences.
   * 
   * It is an error if there no values are provided, or if more than two values are provided.
   */
  Occurs[] value() default {};
  
}
