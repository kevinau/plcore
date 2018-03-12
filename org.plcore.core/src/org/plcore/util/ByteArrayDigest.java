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
package org.plcore.util;

import java.io.Serializable;


public class ByteArrayDigest implements Comparable<Digest>, Serializable, Digest {

  private static final long serialVersionUID = 1L;


  private static final char[] DIGITS = 
    {'0', '1', '2', '3', '4', '5', '6', '7',
     '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


  private final byte[] value;
  
  
  public ByteArrayDigest (byte[] value) {
    this.value = value;
  }

  
  private static byte deHex (char c0, char c1) {
    int i = 0;
    if (c0 <= '9') {
      i += c0 - '0';
    } else {
      i += c0 - 'a' + 10;
    }
    i <<= 4;
    if (c1 <= '9') {
      i += c1 - '0';
    } else {
      i += c1 - 'a' + 10;
    }
    return (byte)i;
  }
  
  
  public ByteArrayDigest (String s) {
    value = new byte[16];
    int j = 0;
    int i = 0;
    while (j < 16) {
      char c0 = s.charAt(i++);
      if (c0 != '-') {
        char c1 = s.charAt(i++);
        value[j++] = deHex(c0, c1);
      }
    }
  }

  
  public byte[] getBytes () {
    return value;
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
    ByteArrayDigest other = (ByteArrayDigest)obj;
    for (int i = 0; i < value.length; i++) {
      if (value[i] != other.value[i]) {
        return false;
      }
    }
    return true;
  }


  @Override
  public int hashCode() {
    int result = value[0];
    for (int i = 1; i < 4; i++) {
      result = (result << 8) | value[i];
    }
    return result;
  } 

  
  @Override
  public String toString() {
    int n = value.length;
    char[] out = new char[n * 2 + 1];
    // two characters form the hex value.
    for (int i = 0, j = 0; i < n; i++) {
      if (i == 2) {
        out[j++] = '-';
      }
      out[j++] = DIGITS[(0xF0 & value[i]) >>> 4];
      out[j++] = DIGITS[0x0F & value[i]];
    }
    return new String(out);
  }


  @Override
  public int compareTo(Digest other) {
    byte[] value2 = ((ByteArrayDigest)other).value;
    int n = 0;
    for (int i = 0; i < value.length; i++) {
      n = (int)(value[i] & 0xff) - (int)(value2[i] & 0xff);
      if (n != 0) {
        return n;
      }
    }
    return 0;
  }

}
