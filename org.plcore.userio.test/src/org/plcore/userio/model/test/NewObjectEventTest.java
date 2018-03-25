package org.plcore.userio.model.test;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.test.Assert;
import org.plcore.test.ITestCase;
import org.plcore.test.Test;
import org.plcore.userio.model.ContainerChangeListener;
import org.plcore.userio.model.IContainerModel;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IModelFactory;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.test.data.Party;

@Component
public class NewObjectEventTest implements ITestCase {
  
  @Reference
  private IModelFactory modelFactory;
  
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
    
    Assert.assertEquals(7, eventCounter.childAddedCount);
    Assert.assertEquals(0, eventCounter.childRemovedCount);
  }  

}
