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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ByteArrayDigestFactory implements DigestFactory {

  private final String algorithm;
  
  private static class DigestOutputStream extends OutputStream {

    private final MessageDigest md;
    
    private DigestOutputStream (String algorithm) {
      try {
        md = MessageDigest.getInstance(algorithm);
      } catch (NoSuchAlgorithmException ex) {
        throw new RuntimeException(ex);
      }
    }
    
    @Override
    public void write(int b) throws IOException {
      md.update((byte)b);
    }
    
    @Override
    public void write(byte[] b) throws IOException {
      md.update(b);
    }
    
    @Override
    public void write(byte[] b, int offset, int len) throws IOException {
      md.update(b, offset, len);
    }
    
    @Override
    public void close () {
    }
    
    @Override
    public void flush() {
    }
    
    private void reset () {
      md.reset();
    }
    
    private byte[] getDigest () {
      return md.digest();
    }
  }
  
  
  protected ByteArrayDigestFactory (String algorithm) {
    this.algorithm = algorithm;
  }
  
  
  @Override
  public Digest getFileDigest (File file) {
    return getFileDigest(file.toPath());
  }
  
  
  @Override
  public Digest getFileDigest (URL url) {
    try {
      return getInputStreamDigest(url.openStream());
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  
  @Override
  public Digest getFileDigest (Path path) {
    try {
      MessageDigest md = MessageDigest.getInstance(algorithm);
      InputStream fis = Files.newInputStream(path);
      byte[] dataBytes = new byte[1024];
      int n = fis.read(dataBytes); 
      while (n != -1) {
        md.update(dataBytes, 0, n);
        n = fis.read(dataBytes);
      }
      fis.close();
      return new ByteArrayDigest(md.digest());
    } catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    } catch (FileNotFoundException ex) {
      throw new RuntimeException(ex);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  

  @Override
  public Digest getInputStreamDigest (InputStream fis) {
    try {
      MessageDigest md = MessageDigest.getInstance(algorithm);
      byte[] dataBytes = new byte[1024];
      int n = fis.read(dataBytes); 
      while (n != -1) {
        md.update(dataBytes, 0, n);
        n = fis.read(dataBytes);
      }
      fis.reset();
      return new ByteArrayDigest(md.digest());
    } catch (NoSuchAlgorithmException ex) {
      throw new RuntimeException(ex);
    } catch (FileNotFoundException ex) {
      throw new RuntimeException(ex);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }
  

  @Override
  public Digest getObjectDigest (Object obj) {
    DigestOutputStream dos = new DigestOutputStream(algorithm);
    dos.reset();
    try (
        ObjectOutputStream oss = new ObjectOutputStream(dos);
        ) {
      oss.writeUnshared(obj);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return new ByteArrayDigest(dos.getDigest());
  }


}
