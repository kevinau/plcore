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
package org.plcore.nio;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * A file writer that cannot create a partly written file.
 * <p>
 * This file writer writes all output to a temporary file.  On "commit"
 * it atomically moves the temporary file to the target file (replacing
 * any existing target file).
 * <p>
 * If the writer does not "commit", the temporary file is removed and 
 * the target file is left unchanged.  In particular, the modified date
 * of the target file is not changed.
 * <p>
 * Note that for this writer, "close" does not "commit".  Merely closing 
 * the writer (either explicitly or via auto close), without a commit,
 * will discard the temporary file and leave the target file unchanged.
 * 
 * @author Kevin Holloway
 *
 */
public class SafeWriter extends Writer {

  private final Path targetPath;
  private final File tempFile;
  private final Writer tempWriter;
  private final String lineSeparator = System.getProperty("line.separator");
  
  private boolean committed = false;
  
  
  public SafeWriter (Path targetPath) {
    this.targetPath = targetPath;
    this.tempFile = new File(targetPath.toString() + ".part");
    this.tempFile.getParentFile().mkdirs();
    try {
      this.tempWriter = new FileWriter(tempFile);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }
  
  
  @Override
  public void close() {
    try {
      tempWriter.close();
      if (committed) {
        Files.move(tempFile.toPath(),  targetPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        committed = false;
      }
      if (tempFile.exists()) {
        tempFile.delete();
      }
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  
  public void commit() {
    committed = true;
  }
  
  
  @Override
  public void flush() throws IOException {
    tempWriter.flush();
  }
  
  
  public void newLine() throws IOException {
    write(lineSeparator);
  }
  
  
  @Override
  public void write(char[] cbuf, int off, int len) throws IOException {
    tempWriter.write(cbuf,  off,  len);
  }

  
  @Override
  public void write(int c) throws IOException {
    tempWriter.write(c);
  }
  

  @Override
  public void write(String str) throws IOException {
    tempWriter.write(str);
  }

}
