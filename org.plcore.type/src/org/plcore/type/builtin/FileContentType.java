/*******************************************************************************
s * Copyright (c) 2012 Kevin Holloway (kholloway@geckosoftware.co.uk).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 *******************************************************************************/
package org.plcore.type.builtin;

import org.plcore.type.UserEntryException;
import org.plcore.value.FileContent;


public class FileContentType extends PathBasedType<FileContent> {
  
  public FileContentType () {
    super ();
  }
  
  
  public FileContentType (int viewSize, String dialogName, String[] filterExtensions, String[] filterNames) {
    super (viewSize, dialogName, filterExtensions, filterNames);
  }
  

  public FileContentType (FileContentType type) {
    super (type);
  }
  
  
  @Override
  public FileContent createFromString(String source) throws UserEntryException {
    String sv = source.trim();
    FileContent fileContent = new FileContent(sv);
    validate(fileContent);
    return fileContent;
  }
  
  
  @Override
  public FileContent newInstance (String source) {
    return new FileContent(source);
  }
  
  
  @Override
  public FileContent primalValue() {
    return new FileContent(".");
  }


  @Override
  public int getFieldSize() {
    return 255;
  }


  @Override
  public Class<?> getFieldClass() {
    return FileContent.class;
  }


//  @Override
//  public String[] getSQLTypes() {
//    return new String[] {
//        "VARCHAR(" + getFieldSize() + ")",
//        "BLOB",
//    };
//  }
//
//
//  @Override
//  public FileContent getFromBuffer(SimpleBuffer b) {
//    throw new NotYetImplementedException();
//  }
//
//  
//  @Override
//  public void putToBuffer (SimpleBuffer b, FileContent v) {
//    throw new NotYetImplementedException();
//  }
//  
//  
//  @Override
//  public int getBufferSize () {
//    throw new NotYetImplementedException();
//  }
//  
//  
//  @Override
//  public String getSQLType() {
//    // Not used
//    return null;
//  }
//
//
//  @Override
//  public void setStatementFromValue (IPreparedStatement stmt, FileContent value) {
//    try {
//      stmt.setString(value.getFileName());
//      // TODO should a IBlob be created to avoid the SQLException
//      Blob blob = stmt.createBlob();
//      blob.setBytes(1, value.getContents());
//      stmt.setBlob(blob);
//    } catch (SQLException ex) {
//      throw new RuntimeException(ex);
//    }
//  }
//
//
// @Override
//  public FileContent getResultValue (IResultSet resultSet) {
//    try {
//      String fileName = resultSet.getString();
//      // TODO should a IBlob be created to avoid the SQLException
//      Blob blob = resultSet.getBlob();
//      byte[] bytes = blob.getBytes(1, (int)blob.length());
//      return new FileContent(fileName, bytes);
//    } catch (SQLException ex) {
//      throw new RuntimeException(ex);
//    }
//  }
  
}
