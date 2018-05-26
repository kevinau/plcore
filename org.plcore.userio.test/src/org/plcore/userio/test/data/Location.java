package org.plcore.userio.test.data;

import org.plcore.userio.Embeddable;
import org.plcore.userio.IOField;


@Embeddable
public class Location {

  @IOField
  private Street street;

  @IOField
  private String suburb;

  public Location() {
    street = new Street();
  }

  public Location(String number, String streetName, String suburb) {
    this.street = new Street(number, streetName);
    this.suburb = suburb;
  }

  public Street getStreet() {
    return street;
  }

  public void setStreet(Street street) {
    this.street = street;
  }

  public String getSuburb() {
    return suburb;
  }

  public void setSuburb(String suburb) {
    this.suburb = suburb;
  }

}
