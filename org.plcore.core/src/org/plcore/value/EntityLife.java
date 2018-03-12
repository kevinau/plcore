/*******************************************************************************
 * Copyright (C) 2018 Kevin Holloway (kholloway@pennyledger.org)
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.plcore.value;

public enum EntityLife {

  /**
   * The entity can be referenced by other entities without restriction.
   */
  ACTIVE,
  
  /**
   * The entity may be still referenced by other entities, but it should not be referenced by new entities.
   * Existing entities cannot be changed to reference a retired entity.
   * <p>
   * A retired entity will be made history when it is not longer referenced by any other entity.
   */
  RETIRED,
  
  /**
   * The entity is not referenced by any other entity and can no longer be used.  It is kept only for
   * historical reference.
   */
  HISTORY;
  
}
