/*******************************************************************************
 * Copyright (C) 2018 Kevin Holloway (kholloway@pennyledger.org)
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.plcore.entity;

/**
 * An enum that captures the life of an entity within an application. An entity
 * has the following life stages:
 */
public enum EntityLife {

  /**
   * The entity is actively used within the application. It can be
   * viewed and changed.
   */
  ACTIVE,

  /**
   * The entity is no longer actively used. In Java
   * terms, it is "deprecated". Retired entities can be viewed, but not editied.
   * No new references to Retired entities can be created. When all references to
   * a Retired entity have been removed, the Retired entity should be removed or 
   * changed to History. A Retired entity can be changed back to Active. 
   */
  RETIRED,

  /**
   * The entity is not actively used, and no other entity references it.
   * It can safely be removed, or it can be retained for historical reference. A
   * History entity cannot be changed back to Active or Retired.
   */
  HISTORY;

}
