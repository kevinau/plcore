package org.plcore.userio.test.data;

import java.time.LocalDate;
import java.util.Date;

import org.plcore.userio.IOField;


public class DateBased {

  @IOField
  public Date date;

  @IOField
  public LocalDate localDate;

  @IOField
  public java.sql.Date sqlDate;

}
