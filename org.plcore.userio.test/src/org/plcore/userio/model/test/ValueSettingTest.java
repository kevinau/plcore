package org.plcore.userio.model.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.plcore.userio.Embeddable;
import org.plcore.userio.IOField;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;

public class ValueSettingTest extends ModelFactorySetup {

  @Embeddable
  public static class Street {

    @IOField
    private String number;
    
    @IOField
    private String streetName;

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

  @Embeddable
  public static class Location {

    @IOField
    private Street street;

    @IOField
    private String suburb;

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

  public static class Party {

    @IOField
    private String name;

    @IOField
    private List<Location> locations;

    public Party(String name, String number, String streetName, String suburb) {
      this.name = name;
      this.locations = new ArrayList<>();
      this.locations.add(new Location(number, streetName, suburb));
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public List<Location> getLocations() {
      return locations;
    }

    public void setLocations(List<Location> locations) {
      this.locations = locations;
    }

  }

  @Test
  public void test() {
    IEntityModel entity = modelFactory.buildEntityModel(Party.class);

    Party party = new Party("Kevin Holloway", "17", "Burwood Avenue", "Nailsworth");
    entity.setValue(party);
  }

  @Test
  public void testItemValues() {
    IEntityModel entity = modelFactory.buildEntityModel(Party.class);

    Party party = new Party("Kevin Holloway", "17", "Burwood Avenue", "Nailsworth");
    entity.setValue(party);

    IItemModel nameModel = entity.selectItemModel("name");
    Assertions.assertNotNull(nameModel);
    String nameValue = nameModel.getValue();
    Assertions.assertEquals("Kevin Holloway", nameValue);

    IItemModel suburbModel = entity.selectItemModel("locations/0/suburb");
    Assertions.assertNotNull(suburbModel);
    String suburbValue = suburbModel.getValue();
    Assertions.assertEquals("Nailsworth", suburbValue);

    IItemModel numberModel = entity.selectItemModel("locations/0/street/number");
    Assertions.assertNotNull(numberModel);
    String numberValue = numberModel.getValue();
    Assertions.assertEquals("17", numberValue);

    numberModel.setValue("19");
    Assertions.assertEquals("19", party.locations.get(0).street.number);

  }

}
