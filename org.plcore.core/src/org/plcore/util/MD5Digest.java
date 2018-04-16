package org.plcore.util;


public class MD5Digest extends ByteArrayDigest {

  private static final long serialVersionUID = 1L;


  public MD5Digest(byte[] value) {
    super(value);
  }

  
  public MD5Digest(String value) {
    super(value);
  }

}
