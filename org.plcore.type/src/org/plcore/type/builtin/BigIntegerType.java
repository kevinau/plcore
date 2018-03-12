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

import java.math.BigInteger;

import org.osgi.service.component.annotations.Component;
import org.plcore.type.IType;
import org.plcore.type.UserEntryException;


@Component(service = IType.class)
public class BigIntegerType extends IntegerBasedType<BigInteger> {
  
  private static final BigInteger DEFAULT_MAX = new BigInteger("999999999999");
  private static final BigInteger DEFAULT_MIN = new BigInteger("-99999999999");
  
  public BigIntegerType () {
    super (DEFAULT_MIN, DEFAULT_MAX);
  }


  public BigIntegerType (BigInteger min, BigInteger max) {
    super (min, max);
  }

  
  public BigIntegerType (BigIntegerType type) {
    super (type);
  }
  

  @Override
  public BigInteger createFromString(String source) throws UserEntryException {
    validateIntegerSource(source);
    return new BigInteger(source);
  }


  @Override
  protected long longValue (BigInteger value) {
    return value.longValue();
  }
  
  
  @Override
  public BigInteger newInstance(String source) {
    return new BigInteger(source);
  }


  @Override
  public BigInteger primalValue() {
    return BigInteger.ZERO;
  }


  @Override
  protected void validate (BigInteger value) throws UserEntryException {
    checkWithinRange(value.longValue());
  }


  @Override
  public Class<?> getFieldClass() {
    return BigInteger.class;
  }

  
//  @Override
//  public BigInteger getFromBuffer (SimpleBuffer b) {
//    String s = b.nextNulTerminatedString();
//    return new BigInteger(s);
//  }
//
//
//  @Override
//  public void putToBuffer (SimpleBuffer b, BigInteger v) {
//    BigInteger v0 = (BigInteger)v;
//    b.appendNulTerminatedString(v0.toString());
//  }
//
//
//  @Override
//  public int getBufferSize () {
//    return BUFFER_NUL_TERMINATED;
//  }
  
  
//  @Override
//  public String getSQLType() {
//    return "DECIMAL(" + getMaxDigits() + ")";
//  }
//
//
//  @Override
//  public void setStatementFromValue(IPreparedStatement stmt, BigInteger value) {
//    stmt.setBigDecimal(new BigDecimal(value));
//  }
//
//
//  @Override
//  public BigInteger getResultValue(IResultSet resultSet) {
//    return resultSet.getBigDecimal().toBigInteger();
//  }

}
