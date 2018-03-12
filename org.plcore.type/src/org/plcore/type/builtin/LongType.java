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
import org.plcore.type.IType;
import org.plcore.type.UserEntryException;

@Component(service = IType.class)
public class LongType extends IntegerBasedType<Long> {
  
  public LongType () {
    super (Long.MIN_VALUE, Long.MAX_VALUE);
  }


  public LongType (long min, long max) {
    super (min, max);
  }


  public LongType (LongType type) {
    super (type);
  }
  
  
  @Override
  public Long createFromString(String source) throws UserEntryException {
    validateIntegerSource(source);
    return Long.parseLong(source);
  }


  @Override
  protected long longValue (Long value) {
    return value;
  }
  
  
  @Override
  public Long newInstance(String source) {
    return Long.parseLong(source);
  }


  @Override
  public Long primalValue() {
    return 0L;
  }


  @Override
  protected void validate (Long value) throws UserEntryException {
    checkWithinRange(value);
  }


  @Override
  public Class<?> getFieldClass() {
    return Long.class;
  }


//  @Override
//  public Long getFromBuffer (SimpleBuffer b) {
//    return getLongFromBuffer(b);
//  }
//
//  
//  @Override
//  public void putToBuffer (SimpleBuffer b, Long v) {
//    putLongToBuffer(b, (long)v);
//  }
//
//  
//  @Override
//  public int getBufferSize () {
//    return Long.BYTES;
//  }
  
  
//  @Override
//  public String getSQLType() {
//    return "BIGINT";
//  }
//
//  @Override
//  public void setStatementFromValue(IPreparedStatement stmt, Long value) {
//    stmt.setLong(value);
//  }
//
//
//  @Override
//  public Long getResultValue(IResultSet resultSet) {
//    return resultSet.getLong();
//  }

}
