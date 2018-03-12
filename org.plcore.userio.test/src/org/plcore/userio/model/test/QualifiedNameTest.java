package org.plcore.userio.model.test;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.test.Assert;
import org.plcore.test.ITestCase;
import org.plcore.test.Test;
import org.plcore.userio.model.IContainerModel;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.IModelFactory;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.test.data.Party;

@Component
public class QualifiedNameTest implements ITestCase {
  
  @Reference
  private IModelFactory modelFactory;
  
  @Test
  public void testQualifiedNames() {
    IEntityModel entity = modelFactory.buildEntityModel(Party.class);
    entity.setValue(new Party("Kevin Holloway", "17", "Burwood Avenue", "Nailsworth"));
    
    StringBuilder builder = new StringBuilder();
    
    IItemModel nameModel = entity.selectItemModel("name");
    Assert.assertNotNull(nameModel);
    String qname = nameModel.getQName();
    Assert.assertEquals("/name", qname);
    qname = nameModel.getQName(entity);
    Assert.assertEquals("name", qname);
    
    builder.setLength(0);
    IItemModel homeSuburbModel = entity.selectItemModel("home/suburb");
    Assert.assertNotNull(homeSuburbModel);
    qname = homeSuburbModel.getQName();
    Assert.assertEquals("/home/suburb", qname);
    qname = homeSuburbModel.getQName(entity);
    Assert.assertEquals("home/suburb", qname);
    IContainerModel homeModel = entity.selectNodeModel("home");
    qname = homeSuburbModel.getQName(homeModel);
    Assert.assertEquals("suburb", qname);
    
    builder.setLength(0);
    List<INodeModel> locationsModel = entity.selectNodeModels("locations/*");
    Assert.assertNotNull(locationsModel);
    Assert.assertEquals(1, locationsModel.size());

    builder.setLength(0);
    IItemModel locationsModel2 = entity.selectItemModel("locations/*/suburb");
    Assert.assertNotNull(locationsModel2);
    qname = locationsModel2.getQName();
    Assert.assertEquals("/locations/0/suburb", qname);

    builder.setLength(0);
    IItemModel suburbModel = entity.selectItemModel("locations/*/*/number");
    Assert.assertNotNull(suburbModel);
    qname = suburbModel.getQName();
    Assert.assertEquals("/locations/0/street/number", qname);
  }

}
