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

import java.math.BigDecimal;

import org.osgi.service.component.annotations.Component;
import org.plcore.type.IType;
import org.plcore.type.NumberSign;
import org.plcore.type.UserEntryException;


@Component(service = IType.class)
public class BigDecimalType extends DecimalBasedType<BigDecimal> {

  public BigDecimalType() {
    super(10, 0);
  }


  public BigDecimalType(int precision) {
    super(precision);
  }


  public BigDecimalType(int precision, int decimals) {
    super(precision, decimals);
  }


  public BigDecimalType(NumberSign sign, int precision) {
    super(sign, precision);
  }


  public BigDecimalType(NumberSign sign, int precision, int decimals) {
    super(sign, precision, decimals);
  }


  public BigDecimalType(BigDecimalType type) {
    super(type);
  }


  @Override
  public BigDecimal createFromString(String source) throws UserEntryException {
    validateDecimalSource(source);
    return new BigDecimal(source);
  }


  @Override
  public BigDecimal newInstance(String source) {
    return new BigDecimal(source);
  }


  @Override
  public BigDecimal primalValue() {
    return BigDecimal.ZERO;
  }


  @Override
  protected void validate(BigDecimal value) throws UserEntryException {
    if (getNumberSign() == NumberSign.UNSIGNED && value.compareTo(BigDecimal.ZERO) < 0) {
      throw new UserEntryException("negative number not allowed");
    }
    // The following may truncate very large numbers
    validatePrecision(value.longValue());
    validateDecimals(value.remainder(BigDecimal.ONE).stripTrailingZeros().scale());
  }


  // @Override
  // public BigDecimal getFromBuffer (SimpleBuffer b) {
  // throw new NotYetImplementedException();
  // }
  //
  //
  // @Override
  // public void putToBuffer (SimpleBuffer b, BigDecimal v) {
  // throw new NotYetImplementedException();
  // }
  //
  //
  // @Override
  // public int getBufferSize () {
  // throw new NotYetImplementedException();
  // }

  @Override
  public BigDecimal newValue() {
    return BigDecimal.ZERO;
  }


  @Override
  public Class<?> getFieldClass() {
    return BigDecimal.class;
  }

  // @Override
  // public void setStatementFromValue(IPreparedStatement stmt, BigDecimal
  // value) {
  // stmt.setBigDecimal(value);
  // }
  //
  //
  // @Override
  // public BigDecimal getResultValue(IResultSet resultSet) {
  // return resultSet.getBigDecimal();
  // }

}
