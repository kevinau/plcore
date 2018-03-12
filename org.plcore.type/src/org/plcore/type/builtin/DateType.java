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


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.osgi.service.component.annotations.Component;
import org.plcore.type.IType;
import org.plcore.type.UserEntryException;


@Component(service = IType.class)
public class DateType extends DateBasedType<Date> {
  
  public DateType () {
  }
  
  
  public DateType (DateType type) {
    super (type);
  }
  
  
  @Override
  public Date primalValue() {
    Calendar calendar = new GregorianCalendar();
    calendar.set(Calendar.YEAR, 0);
    calendar.set(Calendar.MONTH, 0);
    calendar.set(Calendar.DAY_OF_MONTH, 1);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }


  @Override
  public Date today() {
    return new Date();
  }
  
  
  @Override
  protected void validate(Date date) throws UserEntryException {
    // Nothing more to validate
  }


  @Override
  protected Date createFromYearMonthDay(int year, int month, int day) {
    Calendar calendar = new GregorianCalendar();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.DAY_OF_MONTH, day);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MILLISECOND, 0);
    return calendar.getTime();
  }


  @Override
  protected int[] splitDate(Date date) {
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    int[] components = new int[3];
    components[0] = calendar.get(Calendar.YEAR);
    components[1] = calendar.get(Calendar.MONTH) + 1;
    components[2] = calendar.get(Calendar.DAY_OF_MONTH);
    return components;
  }


//  @Override
//  public Date getFromBuffer (SimpleBuffer b) {
//    long n = getLongFromBuffer(b);
//    return new Date(n);
//  }
//
//  
//  @Override
//  public void putToBuffer (SimpleBuffer b, Date v) {
//    Date v0 = (Date)v;
//    putLongToBuffer (b, v0.getTime());
//  }
  
  
//  @Override
//  public void setStatementFromValue (IPreparedStatement stmt, Date value) {
//    stmt.setDate(new java.sql.Date(value.getTime()));
//  }
//
//
//  @Override
//  public Date getResultValue (IResultSet resultSet) {
//    return resultSet.getDate();
//  }


  @Override
  public Class<?> getFieldClass() {
    return Date.class;
  }
 
}

