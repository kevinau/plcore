package org.plcore.type.builtin;

import org.osgi.service.component.annotations.Component;
import org.plcore.type.IType;
import org.plcore.type.UserEntryException;

@Component(service = IType.class)
public class VoidType implements IType<Void> {

  public VoidType () {
  }
  
  
  public VoidType (VoidType type) {
  }

  
  @Override
  public Void createFromString(String source) throws UserEntryException {
    throw new RuntimeException("There should be no user input for a VoidType field");
  }

  @Override
  public Void createFromString(Void fillValue, boolean nullable, boolean creating, String source) throws UserEntryException {
    throw new RuntimeException("There should be no user input for a VoidType field");
  }

  @Override
  public Void newInstance(String source) {
    return null;
  }

  @Override
  public String toDescriptionString(Void value) {
    return null;
  }

  @Override
  public String toEntryString(Void value, Void fillValue) {
    return "";
  }

  @Override
  public String toValueString(Void value) {
    throw new IllegalStateException("There is not value associated with the Void type");
  }

  @Override
  public Void primalValue() {
    return null;
  }

  @Override
  public void validate(Void value, boolean nullable) throws UserEntryException {
  }

  @Override
  public String getRequiredMessage() {
    return null;
  }

  @Override
  public int getFieldSize() {
    return 0;
  }

  @Override
  public boolean isPrimitive() {
    return true;
  }

  @Override
  public Class<?> getFieldClass() {
    return Void.class;
  }

  
//  @Override
//  public Void getFromBuffer (SimpleBuffer b) {
//    throw new IllegalStateException("Void type is never retrieved from a database");
//  }
//
//  
//  @Override
//  public void putToBuffer (SimpleBuffer b, Void v) {
//    throw new IllegalStateException("Void type is never stored in a database");
//  }
//  
//  
//  @Override
//  public int getBufferSize () {
//    throw new IllegalStateException("Void type is never stored in a database");
//  }
  
  
//  @Override
//  public String getSQLType() {
//    throw new IllegalStateException("Void type is never stored in a database");
//  }
//
//  @Override
//  public void setStatementFromValue(IPreparedStatement stmt, Void value) {
//    throw new IllegalStateException("Void type value is never set in the database");
//  }
//
//  @Override
//  public Void getResultValue(IResultSet resultSet) {
//    throw new IllegalStateException("Void type value is never retrieved from the database");
//  }

}
