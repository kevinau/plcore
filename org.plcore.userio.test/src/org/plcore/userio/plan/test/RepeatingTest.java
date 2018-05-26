package org.plcore.userio.plan.test;

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.plcore.userio.Occurs;
import org.plcore.userio.plan.IArrayPlan;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IListPlan;
import org.plcore.userio.plan.INodePlan;


public class RepeatingTest extends PlanFactorySetup {

  public static class Inner {
    Integer inner1;
  }
  
  public static class Outer {
    Integer[] field1;
    
    Inner[] innerArray;
    
    Inner[][] doubleInnerArray;

    List<Integer> field2;
    
    List<Inner> innerList;
  }
  
  @Test
  public void planFactoryTest () {
    Assert.assertNotNull(planFactory);
    IEntityPlan<Outer> plan = planFactory.getEntityPlan(Outer.class);
    INodePlan[] members = plan.getMembers();
    Assert.assertEquals(5, members.length);
    int i = 0;
    Assert.assertTrue(members[i++] instanceof IArrayPlan);
    Assert.assertTrue(members[i++] instanceof IArrayPlan);
    Assert.assertTrue(members[i++] instanceof IArrayPlan);
    Assert.assertTrue(members[i++] instanceof IListPlan);
    Assert.assertTrue(members[i++] instanceof IListPlan);
  }


  public static class OuterWithOccurs {
    Integer[] field0;
    
    @Occurs({2, 10})
    Integer[] field1;
    
    @Occurs(2)
    @Occurs(10)
    Integer[][] doubleArray;

    @Occurs({2, 10})
    Integer[][] doubleArray2;

  }


  @Test
  public void planFactoryWithOccurs () {
    Assert.assertNotNull(planFactory);
    IEntityPlan<OuterWithOccurs> plan = planFactory.getEntityPlan(OuterWithOccurs.class);
    INodePlan[] members = plan.getMembers();
    Assert.assertEquals(4, members.length);
    
    IArrayPlan item0 = (IArrayPlan)plan.selectNodePlan("field0");
    Assert.assertEquals(0, item0.getDimension());
    Assert.assertEquals(0, item0.getMinOccurs());
    Assert.assertEquals(-1, item0.getMaxOccurs());

    IArrayPlan item1 = plan.selectNodePlan("field1");
    Assert.assertEquals(0, item1.getDimension());
    Assert.assertEquals(2, item1.getMinOccurs());
    Assert.assertEquals(10, item1.getMaxOccurs());

    IArrayPlan item2 = plan.selectNodePlan("doubleArray");
    Assert.assertEquals(0, item2.getDimension());
    Assert.assertEquals(2, item2.getMinOccurs());
    Assert.assertEquals(2, item2.getMaxOccurs());

    IArrayPlan item2x = item2.getElementPlan();
    Assert.assertEquals(1, item2x.getDimension());
    Assert.assertEquals(10, item2x.getMinOccurs());
    Assert.assertEquals(10, item2x.getMaxOccurs());

    IArrayPlan item3 = plan.selectNodePlan("doubleArray2");
    Assert.assertEquals(0, item3.getDimension());
    Assert.assertEquals(2, item3.getMinOccurs());
    Assert.assertEquals(10, item3.getMaxOccurs());

    IArrayPlan item3x = item3.getElementPlan();
    Assert.assertEquals(1, item3x.getDimension());
    Assert.assertEquals(0, item3x.getMinOccurs());
    Assert.assertEquals(-1, item3x.getMaxOccurs());

  }

}
