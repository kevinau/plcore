package org.plcore.userio.model.test;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.plcore.test.Assert;
import org.plcore.test.ITestCase;
import org.plcore.test.Test;
import org.plcore.userio.IOField;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.IItemModel;
import org.plcore.userio.model.IModelFactory;

@Component
public class SimpleArrayValueTest implements ITestCase {

  @Reference
  private IModelFactory modelFactory;
  
  public static class Zoo {
    @IOField
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
    Assert.assertEquals(4, elemModels.size());
    
    IItemModel elemModel = entity.selectItemModel("names/0");
    Assert.assertNotNull(elemModel);
    String elemValue = elemModel.getValue();
    Assert.assertEquals("Lion", elemValue);
    
    elemModel = entity.selectItemModel("names/-1");
    Assert.assertNotNull(elemModel);
    elemValue = elemModel.getValue();
    Assert.assertEquals("Bear", elemValue);

    IItemModel elem2Model = entity.selectItemModel("names/2");
    Assert.assertNotNull(elem2Model);
    String elem2Value = elem2Model.getValue();
    Assert.assertEquals("Otter", elem2Value);
    
    // Change the entity model value
    elem2Model.setValue("Seal");
    Assert.assertEquals("Seal", zoo.names[2]);
    
  }

}
