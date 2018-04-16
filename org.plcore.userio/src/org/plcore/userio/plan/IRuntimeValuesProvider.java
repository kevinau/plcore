/*******************************************************************************
 * Copyright (c) 2008 Kevin Holloway (kholloway@geckosoftware.com.au).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.txt
 * 
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 ******************************************************************************/
package org.plcore.userio.plan;

import java.util.List;

import org.plcore.value.ICode;


public interface IRuntimeValuesProvider extends IRuntimeProvider {

  /**
   * Get the list of code values for the target fields. The target fields are
   * those returned by the getAppliesTo method of IRuntimeProvider.
   * 
   * @return the list of code values for the target fields.
   */
  public List<ICode<?>> getCodeValues(Object instance);

}
