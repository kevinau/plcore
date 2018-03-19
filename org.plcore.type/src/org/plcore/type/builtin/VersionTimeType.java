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


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.osgi.service.component.annotations.Component;
import org.plcore.type.IType;
import org.plcore.type.UserEntryException;
import org.plcore.value.VersionTime;


@Component(service = IType.class)
public class VersionTimeType extends StringBasedType<VersionTime> {

  private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSSSSS");

  
  public VersionTimeType () {
  	super (10 + 1 + 8);
  }
  
  
  public VersionTimeType (VersionTimeType type) {
    super (type);
  }
  
  
  @Override
  public VersionTime createFromString (String source) throws UserEntryException {
    throw new UnsupportedOperationException();  
  }
  
  
  @Override
  public VersionTime primalValue () {
    return new VersionTime(0, 0);
  }
  
  
  @Override
  public VersionTime newInstance (String source) {
    try {
      long x = format.parse(source).getTime();
      return new VersionTime(x, 0);
    } catch (ParseException ex) {
      throw new RuntimeException(ex);
    }
  }


  @Override
  protected void validate(VersionTime value) throws UserEntryException {
    // Nothing more to do
  }


//  @Override
//  public String getSQLType() {
//    return "TIMESTAMP";
//  }
//
//
//  @Override
//  public void setStatementFromValue(IPreparedStatement stmt, VersionTime value) {
//    stmt.setTimestamp(new Timestamp(value.getMillis()));
//  }
//
//
//  @Override
//  public VersionTime getResultValue(IResultSet resultSet) {
//    Timestamp t = resultSet.getTimestamp();
//    return new VersionTime(t.getTime());
//  }


  @Override
  public Class<?> getFieldClass() {
    return VersionTime.class;
  }

}

