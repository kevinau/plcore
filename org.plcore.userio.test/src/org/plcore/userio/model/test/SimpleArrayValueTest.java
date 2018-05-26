package org.plcore.userio.model.test;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.plcore.userio.model.ContainerChangeListener;
import org.plcore.userio.model.IContainerModel;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.model.IRepeatingModel;

public class SimpleArrayValueTest extends ModelFactorySetup {

  public static class Zoo {
    private String[] names;
    
    public Zoo (String... names) {
      this.names = names;
    }

    public String[] getNames() {
      return names;
    }
    
    public void setNames(String[] names) {
      this.names = names;
    }

  }
  
  @Test
  public void testElementValues() {
    IEntityModel entity = modelFactory.buildEntityModel(Zoo.class);
    
    Zoo zoo = new Zoo("Lion", "Elephant", "Otter", "Bear");
    entity.setValue(zoo);
    
    List<IItemModel> elemModels = entity.selectItemModels("**");
    Assertions.assertEquals(4, elemModels.size());
    
    IItemModel elemModel = entity.selectItemModel("names/0");
    Assertions.assertNotNull(elemModel);
    String elemValue = elemModel.getValue();
    Assertions.assertEquals("Lion", elemValue);
    
    elemModel = entity.selectItemModel("names/-1");
    Assertions.assertNotNull(elemModel);
    elemValue = elemModel.getValue();
    Assertions.assertEquals("Bear", elemValue);

    IItemModel elem2Model = entity.selectItemModel("names/2");
    Assertions.assertNotNull(elem2Model);
    String elem2Value = elem2Model.getValue();
    Assertions.assertEquals("Otter", elem2Value);
    
    // Change the entity model value
    elem2Model.setValue("Seal");
    Assertions.assertEquals("Seal", zoo.names[2]);
    
  }

 
  public static class Address {
    private String[] lines = new String[0];
  }

  private int changeCount = 0;

  private ContainerChangeListener containerChangeListener = new ContainerChangeListener() {
    @Override
    public void childAdded(IContainerModel parent, INodeModel node) {
      changeCount++;
    }

    @Override
    public void childRemoved(IContainerModel parent, INodeModel node) {
      changeCount--;
    }
  };
  

  @Test
  public void testNewArray() {
    IEntityModel model = modelFactory.buildEntityModel(Address.class);
    model.addContainerChangeListener(containerChangeListener);
    Assertions.assertEquals(0, changeCount);
    
    Address address = model.setNew();
    Assertions.assertEquals(1, changeCount);
    
    IRepeatingModel arrayModel = (IRepeatingModel)model.selectNodeModel("lines");
    arrayModel.addValue("18 Smith Street");
    arrayModel.addValue("Adelaide");
    Assertions.assertEquals(2, address.lines.length);
    Assertions.assertEquals("18 Smith Street", address.lines[0]);
    Assertions.assertEquals("Adelaide", address.lines[1]);
    Assertions.assertEquals(3, changeCount);
  }


}
