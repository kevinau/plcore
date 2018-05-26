package org.plcore.userio.model.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.plcore.userio.model.ContainerChangeListener;
import org.plcore.userio.model.IContainerModel;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.test.data.Party;

public class NewObjectEventTest extends ModelFactorySetup {
  
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
    IEntityModel model = modelFactory.buildEntityModel(Party.class);
  
    EventCounter eventCounter = new EventCounter();
    model.addContainerChangeListener(eventCounter);

    model.setNew();
    
    Assertions.assertEquals(7, eventCounter.childAddedCount);
    Assertions.assertEquals(0, eventCounter.childRemovedCount);
  }  

}
