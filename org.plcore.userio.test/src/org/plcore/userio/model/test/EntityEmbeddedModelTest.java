package org.plcore.userio.model.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.plcore.entity.VersionTime;
import org.plcore.userio.Embedded;
import org.plcore.userio.Entity;
import org.plcore.userio.IOField;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.INodePlan;

public class EntityEmbeddedModelTest extends ModelFactorySetup {

  public static class Location {
    @IOField
    private String street;
    
    @IOField
    private String suburb;
    
    public Location () {
      this ("No street", "No suburb");
    }
    
    public Location (String street, String suburb) {
      this.street = street;
      this.suburb = suburb;
    }


    @Override
    public String toString() {
      return street + ", " + suburb;
    }

  }
  
  @Entity
  public static class Party {

    @IOField
    private int id;
    
    @IOField
    private VersionTime version;
    
    @IOField
    private String name;

    @Embedded
    private Location location;

    public Party() {
      this.name = "No name given";
      this.location = new Location();
    }
    

    public Party(String name, String street, String suburb) {
      this.name = name;
      this.location = new Location(street, suburb);
    }

    public VersionTime getVersion() {
      return version;
    }

    public void setVersion(VersionTime version) {
      this.version = version;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Location getLocation() {
      return location;
    }

    public void setLocation(Location location) {
      this.location = location;
    }

    public int getId() {
      return id;
    }

    @Override
    public String toString() {
      return name + ", " + location;
    }

  }
  
  
  @Test
  public void getPartyPlan () {
    IEntityPlan<Party> plan = planFactory.getEntityPlan(Party.class);
    Assertions.assertNotNull(plan, "Entity plan must not be null");

    INodePlan idPlan = plan.getIdPlan();
    Assertions.assertNotNull(idPlan);
    
    INodePlan versionPlan = plan.getVersionPlan();
    Assertions.assertNotNull(versionPlan);
    
    INodePlan entityLifePlan = plan.getEntityLifePlan();
    // No entity life plan on this entity
    Assertions.assertNull(entityLifePlan);
    
    INodePlan[] members = plan.getMembers();
    // 4 members: id, version, name, location
    Assertions.assertEquals(4, members.length);
  }
  
  @Test 
  public void createEntityModel () {
    IEntityModel model = modelFactory.buildEntityModel(Party.class);
    
    Party instance = new Party("Kevin Holloway", "Burwood Avenue", "Nailsworth");
    model.setValue(instance);
    
    Party instance2 = model.getValue();
    
    Assertions.assertEquals("Kevin Holloway", instance2.name);
    Assertions.assertEquals("Burwood Avenue", instance2.location.street);
    Assertions.assertEquals("Nailsworth", instance2.location.suburb);
  }

  
  @Test 
  public void checkMemberItems () {
    IEntityModel entityModel = modelFactory.buildEntityModel(Party.class);
    
    Party instance = new Party("Kevin Holloway", "Burwood Avenue", "Nailsworth");
    entityModel.setValue(instance);

    IItemModel nameModel = entityModel.selectItemModel("name");
    Assertions.assertEquals("Kevin Holloway", nameModel.getValue());

    IItemModel suburbModel = entityModel.selectItemModel("location/suburb");
    Assertions.assertEquals("Nailsworth", suburbModel.getValue());
  }
}
