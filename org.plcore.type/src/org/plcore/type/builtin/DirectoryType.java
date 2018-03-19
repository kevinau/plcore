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
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.plcore.todo.NotYetImplementedException;
import org.plcore.type.ILengthSettable;
import org.plcore.type.IType;
import org.plcore.type.UserEntryException;


public class DirectoryType extends StringBasedType<File> implements IType<File>, ILengthSettable {

  private final static int DIALOG_SIZE = 40;
  private final static String DIALOG_NAME = "Select directory";
  
  private final int viewSize;
  
  private final String dialogName;
  
  
  public DirectoryType () {
    this (DIALOG_SIZE, DIALOG_NAME);
  }
  
  
  public DirectoryType (int viewSize, String dialogName) {
    super (255);
    if (viewSize == -1) {
      this.viewSize = DIALOG_SIZE;
    } else {
      this.viewSize = viewSize;
    }
    this.dialogName = dialogName;
  }

  
  public DirectoryType (DirectoryType type) {
    super (type);
    this.viewSize = type.viewSize;
    this.dialogName = type.dialogName;
  }
  
  
//  public IEntryControl2 createEntryControl (Composite parent, boolean allowEmpty, boolean primaryKey) {
//    return new FilePathEntryControl(parent, this, viewSize, dialogName, allowEmpty, true);
//  }
//  
//  
//  public IViewControl createViewControl (Composite parent) {
//    return new TextViewControl(parent, viewSize);
//  }
  
  
  @Override
  public File createFromString (String source) throws UserEntryException {
    String sv = source.trim();
    Path path;
    try {
      path = Paths.get(sv);
    } catch (InvalidPathException ex) {
      throw new UserEntryException("Illegal directory path");
    }
    File file = path.toFile();
    validate(file);
    return file;
  }


  @Override
  public void validate (File value) throws UserEntryException {
    if (!value.exists()) {
      throw new UserEntryException("path does not exist");
    }
    if (!value.isDirectory()) {
      throw new UserEntryException("path is not a directory");
    }
  }
  

  public String getDialogName () {
    return dialogName;
  }
  
  
  public int getViewSize () {
    return viewSize;
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
  public Class<?> getFieldClass() {
    throw new NotYetImplementedException();
  }
  
}
