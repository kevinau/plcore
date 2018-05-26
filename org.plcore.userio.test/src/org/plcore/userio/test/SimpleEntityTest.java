package org.plcore.userio.test;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.plcore.type.TypeRegistry;
import org.plcore.userio.plan.EntityLabelGroup;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IItemPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.impl.PlanFactory;
import org.plcore.userio.test.data.SimpleEntity;

public class SimpleEntityTest {

  private PlanFactory planFactory = new PlanFactory();
//  private ModelFactory modelFactory = new ModelFactory();
  
  private IEntityPlan<?> plan;
  
  
  @BeforeEach
  public void before () {
    TypeRegistry typeRegistry = new TypeRegistry();
    typeRegistry.addZeroConfigurationBuiltins();
    
    planFactory = new PlanFactory(typeRegistry);
    plan = planFactory.getEntityPlan(SimpleEntity.class);
  }
  
  
  @Test
  public void testBasicPlan () {    
    INodePlan idPlan = plan.getIdPlan();
    Assertions.assertNotNull(idPlan);
    
    INodePlan versionPlan = plan.getVersionPlan();
    Assertions.assertNotNull(versionPlan);
    
    INodePlan entityLifePlan = plan.getEntityLifePlan();
    Assertions.assertNotNull(entityLifePlan);
    
    List<INodePlan> dataPlans = plan.getDataPlans();
    Assertions.assertEquals(2, dataPlans.size());
  }
  

  @Test
  public void testEntityLabels () {
    EntityLabelGroup labels = plan.getLabels();
    String title = labels.getTitle();
    Assertions.assertEquals("Simple entity", title);
    String description = labels.getDescription();
    Assertions.assertEquals("", description);
  }

  
  @Test
  public void testNodePaths () {
    IItemPlan<?> field1Plan = plan.selectItemPlan("field1");
    Assertions.assertEquals("field1", field1Plan.getName());
    
    IItemPlan<?> field2Plan = plan.selectItemPlan("field2");
    Assertions.assertEquals("field2", field2Plan.getName());

    List<IItemPlan<?>> plans = plan.selectItemPlans("*");
    Assertions.assertEquals(5, plans.size());
  }
  
//  @Test
//  public void testEntityModel () {
//    IEntityModel model = modelFactory.buildEntityModel(plan);
//    model.addContainerChangeListener(new ContainerChangeListener() {
//
//      @Override
//      public void childAdded(IContainerModel parent, INodeModel node, Map<String, Object> ontext) {
//        Assertions.assertEquals(true, parent instanceof IEntityModel);
//        Assertions.assertEquals("SimpleEntity", ((IEntityModel)parent).getName());
//        Assertions.assertEquals(true, node instanceof IItemModel);
//      }
//
//      @Override
//      public void childRemoved(IContainerModel parent, INodeModel node) {
//        // TODO Auto-generated method stub
//      }
//
//    });
//    
//    SimpleEntity instance = new SimpleEntity();
//    instance.name = "Kevin";
//    instance.location = "Australia";
//    model.setValue(instance);
//  }
//
//  
//  @Test
//  public void testBasicModel () {
//    IEntityModel model = modelFactory.buildEntityModel(plan);
//
//    SimpleEntity instance = new SimpleEntity("Kevin", "Nailsworth");
//    model.setValue(instance);
//
//    Assertions.assertEquals(1, model.getNodeId());
//    INodeModel[] members = model.getMembers();
//    Assertions.assertEquals(2, members.length);
//    Assertions.assertTrue(model instanceof INameMappedModel);
//    Assertions.assertEquals(plan, model.getPlan());
//    Assertions.assertEquals(instance, model.getValue());
//    
//    INodeModel field1 = model.getMember("name");
//    Assertions.assertTrue(field1 instanceof IItemModel);
//    Assertions.assertEquals("name", ((IItemModel)field1).getName());
//  }    
//  
//  @Test
//  public void testBasicModelField1 () {
//    IEntityModel model = modelFactory.buildEntityModel(plan);
//
//    SimpleEntity instance = new SimpleEntity("Kevin", "Nailsworth");
//    model.setValue(instance);
//    
//    IItemModel nameModel = model.getMember("name");
//    Assertions.assertEquals(2, nameModel.getNodeId());
//    Assertions.assertEquals("Kevin", nameModel.getValue());
//    
//    IItemPlan<?> namePlan = nameModel.getPlan();
//    IType<?> nameType = namePlan.getType();
//    Assertions.assertTrue(nameType instanceof StringType);
//  }    
//  
//  
//  @Test
//  public void testBasicModelField2 () {
//    IEntityModel model = modelFactory.buildEntityModel(plan);
//
//    SimpleEntity instance = new SimpleEntity();
//    model.setValue(instance);
//    
//    IItemModel nameModel = model.getMember("location");
//    Assertions.assertEquals(3, nameModel.getNodeId());
//    Assertions.assertEquals((String)null, nameModel.getValue());
//    
//    IItemPlan<?> namePlan = nameModel.getPlan();
//    IType<?> nameType = namePlan.getType();
//    Assertions.assertTrue(nameType instanceof StringType);
//  }    
  
}
