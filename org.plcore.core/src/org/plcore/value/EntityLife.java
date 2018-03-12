/*******************************************************************************
 * Copyright (C) 2018 Kevin Holloway (kholloway@pennyledger.org)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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
