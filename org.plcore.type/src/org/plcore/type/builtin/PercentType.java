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

import org.osgi.service.component.annotations.Component;
import org.plcore.math.Decimal;
import org.plcore.math.Percent;
import org.plcore.type.IType;
import org.plcore.type.NumberSign;
import org.plcore.type.UserEntryException;


@Component(service = IType.class)
public class PercentType extends DecimalBasedType<Percent> {

  public PercentType () {
    super (3, 0);
  }
  
  
  public PercentType (NumberSign sign, int precision, int scale) {
    super (sign, precision, scale);
  }

  
  public PercentType (NumberSign sign, int precision) {
    super (sign, precision);
  }

  
  public PercentType (int precision, int scale) {
    super (precision, scale);
  }

  
  public PercentType (int precision) {
    super (precision);
  }

  
  public PercentType (PercentType type) {
    super (type);
  }
  
  
  @Override
  public Percent primalValue() {
    return Percent.ZERO;
  }


  @Override
  public Percent newInstance(String source) {
    return new Percent(source);
  }


  @Override
  public Percent createFromString(String source) throws UserEntryException {
    source = source.trim();
    if (source.endsWith("%")) {
      int n = source.length();
      source = source.substring(0, n - 1).trim();
    }
    validateDecimalSource(source);
    return new Percent(source);
  }


  @Override
  protected void validate(Percent value) throws UserEntryException {
    Decimal d = value.toDecimal();
    validatePrecision(d.longValue());
    validateDecimals(d.trim().getScale());
  }


  @Override
  public Class<?> getFieldClass() {
    return Percent.class;
  }


//  @Override
//  public Percent getFromBuffer (SimpleBuffer b) {
//    int s1 = getIntFromBuffer(b);
//    long v1 = getLongFromBuffer(b);
//    if (s1 < 0) {
//      s1 = -s1;
//    }
//    s1 = 64 - s1;
//    Decimal v = new Decimal(v1, s1).trim();
//    return new Percent(v);
//  }
//
//  
//  @Override
//  public void putToBuffer (SimpleBuffer b, Percent v) {
//    Decimal normal = v.toDecimal().normalize();
//    long v1 = normal.getRawLong();
//    // The scale here is positive, with a higher number representing a higher value
//    int s1 = 64 - normal.getScale();
//
//    if (v1 < 0) {
//      s1 = -s1;
//    }
//    putIntToBuffer (b, s1);
//    putLongToBuffer (b, v1);
//  }
//  
//  
//  @Override
//  public int getBufferSize () {
//    return Integer.BYTES + Long.BYTES;
//  }
  
  
//  @Override
//  public void setStatementFromValue(IPreparedStatement stmt, Percent value) {
//    stmt.setBigDecimal(new BigDecimal(value.toString()));
//  }
//
//
//  @Override
//  public Percent getResultValue(IResultSet resultSet) {
//    BigDecimal bd = resultSet.getBigDecimal();
//    return new Percent(bd.toString());
//  }

}
