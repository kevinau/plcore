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

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;


public class PasswordValue implements Serializable {

  private static final long serialVersionUID = 7369016814827844772L;

  private static final char[] AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

  private static final String ALGORITHM = "SHA1";

  private final String salt;
  private final byte[] digest;


  private static String randomString() {
    Random rnd = new Random();
    char[] sb = new char[16];
    for (int i = 0; i < sb.length; i++) {
      sb[i] = AB[rnd.nextInt(AB.length)];
    }
    return new String(sb);
  }


  public PasswordValue(String password) {
    salt = randomString();
    digest = computeDigest(salt, password);
  }


  @Override
  public String toString() {
    return toHex(digest);
  }

  private static final char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
      'f', };


  private static String toHex(byte[] value) {
    char[] buff = new char[value.length * 2];
    int j = 0;
    for (int i = 0; i < value.length; i++) {
      buff[j++] = hexChar[value[i] & 0xf];
      buff[j++] = hexChar[(value[i] >> 4) & 0xf];
    }
    return new String(buff);
  }


  private byte[] computeDigest(String salt, String password) {
    MessageDigest digester = null;
    try {
      digester = MessageDigest.getInstance(ALGORITHM);
    } catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    }
    digester.reset();
    String x = password + salt;
    digester.update(x.getBytes());
    byte[] hash = digester.digest();
    return hash;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(digest);
    result = prime * result + salt.hashCode();
    return result;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof PasswordValue)) {
      return false;
    }
    PasswordValue other = (PasswordValue)obj;
    if (!Arrays.equals(digest, other.digest)) {
      return false;
    }
    if (!salt.equals(other.salt)) {
      return false;
    }
    return true;
  }


  public boolean matches(String password) {
    byte[] digest2 = computeDigest(salt, password);
    return Arrays.equals(digest, digest2);
  }

}
