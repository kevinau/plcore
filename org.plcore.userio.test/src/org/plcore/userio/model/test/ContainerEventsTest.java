package org.plcore.userio.model.test;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.test.Assert;
import org.plcore.test.ITestCase;
import org.plcore.test.Test;
import org.plcore.userio.Entity;
import org.plcore.userio.IOField;
import org.plcore.userio.Optional;
import org.plcore.userio.model.ContainerChangeListener;
import org.plcore.userio.model.IContainerModel;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IModelFactory;
import org.plcore.userio.model.INodeModel;
import org.plcore.value.EntityLife;
import org.plcore.value.VersionTime;

@Component
public class ContainerEventsTest implements ITestCase {
  
  @Reference
  private IModelFactory modelFactory;
  
  @Entity
  public static class StandardEntity {

    @IOField
    private int id;
    
    @IOField
    private VersionTime version;

    @IOField
    private String name;

    @IOField
    private String location;

    @IOField
    private EntityLife entityLife;

    public StandardEntity() {
      this.name = "No name given";
      this.location = null;
    }

    public StandardEntity(String name, String location) {
      this.name = name;
      this.location = location;
    }

    public StandardEntity(int id, String name, String location) {
      this.id = id;
      this.version = VersionTime.now();
      this.name = name;
      this.location = location;
      this.entityLife = EntityLife.ACTIVE;
    }

    @Override
    public String toString() {
      return name + ", " + location;
    }

    public int getId() {
      return id;
    }

    public void setId(int id) {
      this.id = id;
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

    @Optional
    public String getLocation() {
      return location;
    }

    public void setLocation(String location) {
      this.location = location;
    }

    public EntityLife getEntityLife() {
      return entityLife;
    }

    public void setEntityLife(EntityLife entityLife) {
      this.entityLife = entityLife;
    }

  }
  
  private class EventCounter implements ContainerChangeListener {

    private int childAddedCount = 0;
    private int childRemovedCount = 0;
    
    @Override
    public void childAdded(IContainerModel parent, INodeModel node) {
      childAddedCount++;
    }

    @Override
    public void childRemoved(IContainerModel parent, INodeModel node) {
      childRemovedCount++;
    }
    
  }
  
  
  @Test
  public void testContainerEvents () {
    IEntityModel model = modelFactory.buildEntityModel(StandardEntity.class);
  
    EventCounter eventCounter = new EventCounter();
    model.addContainerChangeListener(eventCounter);

    model.setValue(null);
    Assert.assertEquals(0, eventCounter.childAddedCount);
    Assert.assertEquals(0, eventCounter.childRemovedCount);

    StandardEntity instance = new StandardEntity(123, "Kevin Holloway", "Nailsworth");
    model.setValue(instance);
    Assert.assertEquals(5, eventCounter.childAddedCount);
    Assert.assertEquals(0, eventCounter.childRemovedCount);
    
    model.setValue(null);
    Assert.assertEquals(5, eventCounter.childAddedCount);
    Assert.assertEquals(5, eventCounter.childRemovedCount);
  }  

}
