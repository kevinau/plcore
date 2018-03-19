package org.plcore.type.builtin;

import java.text.MessageFormat;

import org.plcore.type.ICaseSettable;
import org.plcore.type.ILengthSettable;
import org.plcore.type.TextCase;
import org.plcore.type.Type;
import org.plcore.type.UserEntryException;


public abstract class StringBasedType<T> extends Type<T> implements ILengthSettable, ICaseSettable {

  private int maxLength = 255;
  private TextCase allowedCase = TextCase.MIXED;

  
  protected StringBasedType() {
  }

  
  protected StringBasedType (int maxLength) {
    this.maxLength = maxLength;
  }

  
  protected StringBasedType (int maxLength, TextCase allowedCase) {
    this.maxLength = maxLength;
    this.allowedCase = allowedCase;
  }


  protected StringBasedType (StringBasedType<T> type) {
    super(type);
    this.maxLength = type.maxLength;
    this.allowedCase = type.allowedCase;
  }
  
  
  @Override
  public T createFromString(String source) throws UserEntryException {
    int n = source.length();
    if (n > maxLength) {
      String msg = MessageFormat.format("more than {0} characters", Integer.toString(maxLength));
      throw new UserEntryException(msg);
    }
    switch (getAllowedCase()) {
    case UPPER:
      source = source.toUpperCase();
      break;
    case LOWER:
      source = source.toLowerCase();
      break;
    case MIXED :
    case UNSPECIFIED :
      break;
    }
    return createFromString2(source);
  }

  
  @Override
  public void setAllowedCase(TextCase allowedCase) {
    this.allowedCase = allowedCase;
  }

  
  protected TextCase getAllowedCase() {
    return this.allowedCase;
  }
  
  
  @Override
  public void setMaxLength (int maxLength) {
    this.maxLength = maxLength;
  }

  
  @Override
  public int getFieldSize () {
    return maxLength;
  }
  
//  protected void validateString(String source, boolean creating) throws UserEntryException {
//    int n = source.length();
//    if (n > getMaxSize()) {
//      String msg = MessageFormat.format("more than {0} characters", Integer.toString(getMaxSize()));
//      throw new UserEntryException(msg);
//    } else if (n > 0 && source.charAt(0) == ' ') {
//      if (source.charAt(n - 1) == ' ') {
//        throw new UserEntryException("cannot start or end with a space");
//      } else {
//        throw new UserEntryException("cannot start with a space");
//      }
//    } else if (n > 0 && source.charAt(n - 1) == ' ') {
//      throw new UserEntryException("cannot end with a space", true);
//    }
//  }

  
//  @Override
//  public T getFromBuffer (SimpleBuffer b) {
//    String v = b.nextNulTerminatedString();
//    try {
//      return createFromString2(v);
//    } catch (UserEntryException ex) {
//      throw new RuntimeException("Illegal value: " + v);
//    }
//  }
//
//  
//  @Override
//  public void putToBuffer (SimpleBuffer b, T v) {
//    b.appendNulTerminatedString(v.toString());
//  }
//  
//  
//  @Override
//  public int getBufferSize () {
//    return BUFFER_NUL_TERMINATED;
//  }
  
  
//  @Override
//  public String getSQLType() {
//    return "VARCHAR(" + getFieldSize() + ")";
//  }

  
  protected T createFromString2(String source) throws UserEntryException {
    T value = newInstance(source);
    validate (value);
    return value;
  }

  
  @Override
  public abstract T primalValue();


//  @Override
//  public void setStatementFromValue (IPreparedStatement stmt, T value) {
//    stmt.setString(value.toString());
//  }
//
//
//  @SuppressWarnings("unchecked")
//  @Override
//  public T getResultValue (IResultSet resultSet) {
//    return (T)resultSet.getString();
//  }
  
}  
