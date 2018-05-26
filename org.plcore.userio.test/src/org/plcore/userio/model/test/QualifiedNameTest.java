package org.plcore.userio.model.test;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.plcore.userio.model.IContainerModel;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.test.data.Party;

public class QualifiedNameTest extends ModelFactorySetup {
  
  @Test
  public void testQualifiedNames() {
    IEntityModel entity = modelFactory.buildEntityModel(Party.class);
    entity.setValue(new Party("Kevin Holloway", "17", "Burwood Avenue", "Nailsworth"));
    
    StringBuilder builder = new StringBuilder();
    
    IItemModel nameModel = entity.selectItemModel("name");
    Assertions.assertNotNull(nameModel);
    String qname = nameModel.getQName();
    Assertions.assertEquals("/name", qname);
    qname = nameModel.getQName(entity);
    Assertions.assertEquals("name", qname);
    
    builder.setLength(0);
    IItemModel homeSuburbModel = entity.selectItemModel("home/suburb");
    Assertions.assertNotNull(homeSuburbModel);
    qname = homeSuburbModel.getQName();
    Assertions.assertEquals("/home/suburb", qname);
    qname = homeSuburbModel.getQName(entity);
    Assertions.assertEquals("home/suburb", qname);
    IContainerModel homeModel = entity.selectNodeModel("home");
    qname = homeSuburbModel.getQName(homeModel);
    Assertions.assertEquals("suburb", qname);
    
    builder.setLength(0);
    List<INodeModel> locationsModel = entity.selectNodeModels("locations/*");
    Assertions.assertNotNull(locationsModel);
    Assertions.assertEquals(1, locationsModel.size());

    builder.setLength(0);
    IItemModel locationsModel2 = entity.selectItemModel("locations/*/suburb");
    Assertions.assertNotNull(locationsModel2);
    qname = locationsModel2.getQName();
    Assertions.assertEquals("/locations/0/suburb", qname);

    builder.setLength(0);
    IItemModel suburbModel = entity.selectItemModel("locations/*/*/number");
    Assertions.assertNotNull(suburbModel);
    qname = suburbModel.getQName();
    Assertions.assertEquals("/locations/0/street/number", qname);
  }

}
