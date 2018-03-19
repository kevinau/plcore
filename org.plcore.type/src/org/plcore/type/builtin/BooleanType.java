/*******************************************************************************
ow * Copyright (c) 2012 Kevin Holloway (kholloway@geckosoftware.co.uk).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 *******************************************************************************/
package org.plcore.type.builtin;

import org.osgi.service.component.annotations.Component;
import org.plcore.type.IType;
import org.plcore.type.Type;
import org.plcore.type.UserEntryException;

@Component(service = IType.class)
public class BooleanType extends Type<Boolean> implements IType<Boolean> {
  
  private final String checkedLabel;
  
  
  public BooleanType () {
    this ("");
  }
  
  
  public BooleanType (String checkedLabel) {
    this.checkedLabel = checkedLabel;
  }

  
  public BooleanType (BooleanType type) {
    super (type);
    this.checkedLabel = type.checkedLabel;
  }
  
  
  @Override
  public Boolean primalValue() {
    return Boolean.FALSE;
  }


  @Override
  public String toEntryString(Boolean value, Boolean fillValue) {
    if (value == null) {
      return "";
    }
    boolean v = (Boolean) value;
    return v ? "Y" : "N";
  }

  @Override
  public String toValueString(Boolean value) {
    if (value == null) {
      throw new IllegalArgumentException("value cannot be null");
    }
    boolean v = (Boolean) value;
    return v ? "true" : "false";
  }

  /**
   * Return the description of a value. Can return <code>null</code> if it does
   * not have a description that is distinct from its entry representation. If a
   * non-null value is returned, it should not duplicate the entry
   * representation.
   */
  @Override
  public String toDescriptionString(Boolean value) {
    boolean v = (Boolean) value;
    return v ? "Checked" : "Un-checked";
  }

  
  @Override
  public Boolean createFromString (String source) throws UserEntryException {
    Boolean value;
    char c = source.charAt(0);
    switch (c) {
    case 'Y':
    case 'y':
    case '1':
      value = Boolean.TRUE;
      break;
    case 'N':
    case 'n':
    case '0':
      value = Boolean.FALSE;
      break;
    default:
      throw new UserEntryException("not Yes/Y/1 or No/N/0");
    }
    return value;
  }


  @Override
  protected void validate (Boolean value) throws UserEntryException {
    // No additional validation required
  }

  
  public String getCheckedLabel () {
    return checkedLabel;
  }


  @Override
  public Boolean newInstance(String source) {
    try {
      return createFromString(source);
    } catch (UserEntryException ex) {
      throw new RuntimeException(ex.getMessage());
    }
  }


  @Override
  public int getFieldSize() {
    return 1;
  }


//  @Override
//  public Boolean getFromBuffer (SimpleBuffer b) {
//    int v = b.next();
//    return (v == 1);
//  }
//
//  
//  @Override
//  public void putToBuffer (SimpleBuffer b, Boolean v) {
//    b.ensureCapacity(1);
//    
//    boolean v0 = (boolean)v;
//    b.append(v0 ? (byte)1 : (byte)0);
//  }
//  
//  
//  @Override
//  public int getBufferSize () {
//    return 1;
//  }
  
  
//  @Override
//  public String getSQLType() {
//    return "BOOLEAN";
//  }
//
//
//  @Override
//  public void setStatementFromValue(IPreparedStatement stmt, Boolean value) {
//    stmt.setBoolean((Boolean)value);
//  }
//
//
//  @Override
//  public Boolean getResultValue(IResultSet resultSet) {
//    return resultSet.getBoolean();
//  }


  @Override
  public Class<?> getFieldClass() {
    return Boolean.class;
  }
  
}
