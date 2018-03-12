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


import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


public class VersionTime {

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSSSSS");
  
  private final Instant instant;

  
  public VersionTime () {
    this.instant = Instant.now();
  }
  
  
  public VersionTime (long millis) {
    this.instant = Instant.ofEpochMilli(millis);
  }
  
  
  public VersionTime (long seconds, int nanos) {
    this.instant = Instant.ofEpochSecond(seconds, nanos);
  }
  
  
  public static VersionTime now () {
    return new VersionTime();
  }
  
  
  public long getSeconds () {
    return instant.getEpochSecond();
  }
  
  
  public int getNanos () {
    return instant.getNano();
  }
  
  
  public long getMillis () {
    return instant.toEpochMilli();
  }
  
  
  @Override
  public String toString() {
    return formatter.withZone(ZoneId.systemDefault()).format(instant);
  }
  
  
  @Override
  public int hashCode() {
    return instant.hashCode();
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    VersionTime other = (VersionTime)obj;
    return instant.equals(other.instant);
  }
  
}
