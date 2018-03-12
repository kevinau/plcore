/*******************************************************************************
 * Copyright (c) 2012 Kevin Holloway (kholloway@geckosoftware.co.uk).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 *******************************************************************************/
package org.plcore.type.builtin;

import java.io.File;

import org.plcore.type.UserEntryException;


public class PathType extends PathBasedType<File> {

  public PathType () {
    super ();
  }
  
  
  public PathType (int viewSize, String dialogName, String[] filterExtensions, String[] filterNames) {
    super (viewSize, dialogName, filterExtensions, filterNames);
  }


  public PathType (PathType type) {
    super (type);
  }
  
  
  @Override
  public File createFromString (String source) throws UserEntryException {
    String sv = source.trim();
    File file = new File(sv);
    validate (file);
    return file;
  }


  @Override
  public File primalValue() {
    String userHome = System.getProperty("user.home");
    return new File(userHome);
  }


  @Override
  public File newInstance(String source) {
    return new File(source);
  }

  
  @Override
  public int getFieldSize() {
    return 255;
  }

  
//  @Override
//  public File getFromBuffer (SimpleBuffer b) {
//    throw new NotYetImplementedException();
//  }
//
//  
//  @Override
//  public void putToBuffer (SimpleBuffer b, File v) {
//    throw new NotYetImplementedException();
//  }
//
//  
//  @Override
//  public int getBufferSize () {
//    throw new NotYetImplementedException();
//  }
  
  
//  @Override
//  public String getSQLType() {
//    return "VARCHAR(" + getFieldSize() + ")";
//  }
//
//
//  @Override
//  public void setStatementFromValue(IPreparedStatement stmt, File value) {
//    stmt.setString(value.toString());
//  }
//
//
//  @Override
//  public File getResultValue(IResultSet resultSet) {
//    return new File(resultSet.getString());
//  }


  @Override
  public Class<?> getFieldClass() {
    return File.class;
  }

}
