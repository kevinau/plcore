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

import java.util.Collections;
import java.util.List;

import org.plcore.type.ICaseSettable;
import org.plcore.type.ILengthSettable;
import org.plcore.type.IPatternSettable;
import org.plcore.type.IType;
import org.plcore.type.TextCase;
import org.plcore.type.Type;
import org.plcore.type.UserEntryException;
import org.plcore.value.ICode;


public class CodeType<T extends ICode<T>> extends Type<T> implements IType<T>, ICaseSettable, ILengthSettable, IPatternSettable {

  // TODO is codeClass required?  It is not used.
  private final Class<T> codeClass;
  
  private RegexStringType stringType = new RegexStringType();
    
  private List<T> values;

  public CodeType () {
    this.codeClass = null;
    this.values = null;
  }
  
  public CodeType (Class<T> codeClass) {
    this.codeClass = codeClass;
    this.values = Collections.emptyList();
  }

  
  public CodeType (Class<T> codeClass, List<T> values) {
    this.codeClass = codeClass;
    this.values = values;
  }

  
  public CodeType (CodeType<T> type) {
    super (type);
    this.codeClass = type.codeClass;
    this.values = type.values;
  }
  
  
  public void setValues(List<T> values) {
    this.values = values;
  }
  
  
  protected List<T> getValues() {
    if (values == null) {
      throw new IllegalStateException("No values supplied to this type, so a custom getValues should be implemented");
    }
    return values;
  }
  
  
  //public Class<T> getCodeClass() {
  //  return codeClass;
  //}
  
  
  // public IEntryControl2 createEntryControl (Composite parent, boolean
  // allowEmpty, boolean primaryKey) {
  // CodeEntryControl control = new CodeEntryControl(parent, valueFactory, null,
  // allowEmpty, primaryKey);
  // return control;
  // }
  //
  //
  // public IViewControl createViewControl (Composite parent) {
  // return new EnumViewControl(parent, valueFactory);
  // }

  @Override
  public T createFromString(boolean creating, String source) throws UserEntryException {
    if (creating) {
      String cx = stringType.createFromString(source);
      return createFromString(cx);
    } else {
      for (T cv : getValues()) {
        String code = cv.getCode();
        if (code.equals(source)) {
          return cv;
        }
      }
      throw new UserEntryException(getOneOfMessage(true));
    }
  }

  
  public T valueOf (String target) {
    for (T cv : getValues()) {
      String code = cv.getCode();
      if (code.equals(target)) {
        return cv;
      }
    }
    return null;
  }
  
  
  @Override
  public T createFromString(String source) throws UserEntryException {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public T createFromString(T fillValue, boolean nullable, boolean creating, String source) throws UserEntryException {
    if (source.length() == 0) {
      if (nullable) {
        return null;
      } else {
        throw UserEntryException.REQUIRED;
      }
    }

    if (creating) {
      String cx = stringType.createFromString(fillValue.toString(), nullable, source);
      return createFromString(cx);
    } else {
      int incompl = 0;
      String incomplx = null;

      for (T cv : getValues()) {
        String code = cv.getCode();
        if (code.equals(source)) {
          return cv;
        }
        if (code.startsWith(source)) {
          // This is a possible incomplete
          if (incompl == 0) {
            incomplx = code.substring(source.length());
          }
          incompl++;
        }
      }

      switch (incompl) {
      case 0 :
        throw new UserEntryException(getOneOfMessage(true));
      case 1 :
        throw new UserEntryException(getOneOfMessage(false), UserEntryException.Type.INCOMPLETE, incomplx);
      default :
        throw new UserEntryException(getOneOfMessage(false), UserEntryException.Type.INCOMPLETE);
      }
    }
  }

  @Override
  public String toEntrySource(T value, T fillValue) {
    if (value == null) {
      return "";
    }
    return value.getCode();
  }

  @Override
  public String toValueString(T value) {
    if (value == null) {
      throw new IllegalArgumentException("value must not be null");
    }
    return value.getCode();
  }

  @Override
  public String toDisplayString(T value) {
    if (value == null) {
      return "";
    }
    return value.getDescription();
  }

  @Override
  public int getFieldSize() {
    int n = 0;
    for (ICode<T> cv : getValues()) {
      String code = cv.getCode();
      n = Integer.max(n, code.length());
    }
    return n;
  }

  @Override
  public T primalValue() {
    List<T> values = getValues();
    if (values.isEmpty()) {
      return null;
    } else {
      return values.get(0);
    }
  }

  @Override
  public void validate(T value, boolean nullable) throws UserEntryException {
    if (value == null) {
      if (isNullable()) {
        return;
      } else {
        throw new UserEntryException("missing value");
      }
    }
    for (T v : getValues()) {
      if (v.equals(value)) {
        return;
      }
    }
    throw new UserEntryException("'" + value.getCode() + "' is an illegal value");
  }

  @Override
  public T newInstance(String code) {
    for (T v : values) {
      if (v.getCode().equals(code)) {
        return v;
      }
    }
    throw new RuntimeException("Illegal value: " + code);
  }

  private String getOneOfMessage(boolean isError) {
    StringBuilder buffer = new StringBuilder();
    if (isError) {
      buffer.append("not ");
    }
    buffer.append("one of: ");
    int i = 0;
    List<T> values = getValues();
    int n = values.size() - 1;
    for (T cv : values) {
      if (i > 0) {
        if (i == n) {
          buffer.append(" or ");
        } else {
          buffer.append(", ");
        }
      }
      buffer.append(cv.getCode());
    }
    if (!isError) {
      buffer.append(" is required");
    }
    return buffer.toString();
  }

//  @Override
//  public T getFromBuffer(SimpleBuffer b) {
//    String s = b.nextNulTerminatedString();
//    try {
//      return createFromString(s);
//    } catch (UserEntryException ex) {
//      throw new RuntimeException("Illegal value: " + s);
//    }
//  }
//
//  @Override
//  public void putToBuffer(SimpleBuffer b, T v) {
//    b.appendNulTerminatedString(v.toString());
//  }
//
//  @Override
//  public int getBufferSize() {
//    return BUFFER_NUL_TERMINATED;
//  }

//  @Override
//  public String getSQLType() {
//    return "VARCHAR(" + getFieldSize() + ")";
//  }
//
//  @Override
//  public void setStatementFromValue(IPreparedStatement stmt, T value) {
//    stmt.setString(value.getCode());
//  }
//
//  @Override
//  public T getResultValue(IResultSet resultSet) {
//    String s = resultSet.getString();
//    try {
//      return createFromString(s);
//    } catch (UserEntryException ex) {
//      throw new RuntimeException("Illegal value: " + s);
//    }
//  }

  @Override
  protected void validate(T value) throws UserEntryException {
    // Nothing to do?
  }

  
  @Override
  public void setAllowedCase(TextCase allowedCase) {
    stringType.setAllowedCase(allowedCase);
  }

  
  @Override
  public void setPattern (String pattern, String targetName) {
    stringType.setPattern(pattern, targetName);
  }
  
  
  @Override
  public void setMaxLength(int maxLength) {
    stringType.setMaxLength(maxLength);
  }

}
