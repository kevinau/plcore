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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CleanTextReader implements AutoCloseable {

  private final BufferedReader reader;

  public CleanTextReader (String filePath) throws FileNotFoundException {
    reader = new BufferedReader(new FileReader(filePath));
  }


  public CleanTextReader (File file) throws FileNotFoundException {
    reader = new BufferedReader(new FileReader(file));
  }


  /** 
   * Get a line of text from the file, skipping blank lines and comments. 
   * Comments are lines that start with a '#' character.  
   * <p>
   * When determining if a line is
   * blank or a comment, all leading and trailing whitespace is first removed.
   * @Return a text line, stripped of all leading and trailing whitespace.
   */
  public String readLine() throws IOException {
    String line = reader.readLine();
    while (line != null) {
      line = line.trim();
      if (line.length() > 0 && line.charAt(0) != '#') {
        return line;
      }
      line = reader.readLine();
    }
    return null;
  }


  @Override
  public void close() throws IOException {
    reader.close();
  }

}
    

