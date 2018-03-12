package org.plcore.userio.test.data;

import org.plcore.userio.Embeddable;
import org.plcore.userio.IOField;


@Embeddable
public class Street {

  @IOField
  private String number;

  @IOField
  private String streetName;

  public Street() {
  }

  public Street(String number, String streetName) {
    this.number = number;
    this.streetName = streetName;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getStreetName() {
    return streetName;
  }

  public void setStreetName(String streetName) {
    this.streetName = streetName;
  }

}
