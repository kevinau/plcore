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
