package org.plcore.userio.plan.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.plcore.entity.Describing;
import org.plcore.entity.EntityLife;
import org.plcore.entity.EntityLifeType;
import org.plcore.entity.IExplicitDescription;
import org.plcore.entity.Identifiable;
import org.plcore.entity.VersionTime;
import org.plcore.entity.VersionTimeType;
import org.plcore.type.IType;
import org.plcore.userio.Id;
import org.plcore.userio.UniqueConstraint;
import org.plcore.userio.Version;
import org.plcore.userio.plan.EntityLabelGroup;
import org.plcore.userio.plan.IAugmentedClass;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IItemPlan;
import org.plcore.userio.plan.ILabelGroup;
import org.plcore.userio.plan.INodePlan;
import org.plcore.userio.plan.PlanStructure;

import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;


public class EntityPlan<T> extends EmbeddedPlan<T> implements IEntityPlan<T> {

  private final IAugmentedClass<T> aclass;
  private final String entityName;
  private final EntityLabelGroup labels;

  private IItemPlan<Integer> idPlan;
  private IItemPlan<VersionTime> versionPlan;
  private IItemPlan<EntityLife> entityLifePlan;
  private List<INodePlan> dataPlans;
  private List<IItemPlan<?>> describingItemPlans = new ArrayList<>();
  private List<IItemPlan<?>> identifyingItemPlans = new ArrayList<>();
  private IItemPlan<?> firstItemPlan;
  
  
  private List<IItemPlan<?>[]> uniqueConstraints;

  
  public EntityPlan (IAugmentedClass<T> aclass) {
    super (aclass);
    this.aclass = aclass;
    this.entityName = aclass.getSourceClass().getSimpleName();
    this.labels = new EntityLabelGroup(aclass.getSourceClass());
  }

  
  @Override
  public void complete (PlanFactory planFactory) {
    //super.complete(planFactory);
    
    findEntityItems();
    //findDescriptionItems();
    findUniqueConstraints();
  }

  @Override
  public String getEntityName() {
    return entityName;
  }


  @Override
  public ILabelGroup getLabels () {
    return labels;
  }
  
  
  @Override
  public IItemPlan<Integer> getIdPlan () {
    return idPlan;
  }
  
  @Override
  public boolean hasId() {
    return idPlan != null;
  }
  
  
  @Override
  public int getId (Object instance) {
    return idPlan.getFieldValue(instance);
  }
  
  @Override
  public void setId (Object instance, int id) {
    idPlan.setFieldValue(instance, id);
  }
  
  @Override
  public IItemPlan<VersionTime> getVersionPlan () {
    return versionPlan;
  }
  
  @Override
  public boolean hasVersion () {
    return versionPlan != null;
  }
  
  @Override
  public Timestamp getVersion (Object instance) {
    return versionPlan.getFieldValue(instance);
  }
  
  @Override
  public void setVersion (Object instance, Timestamp version) {
    if (versionPlan != null) {
      versionPlan.setFieldValue(instance, version);
    }
  }
  
  
  @Override
  public IItemPlan<EntityLife> getEntityLifePlan () {
    return entityLifePlan;
  }
  
  
  @Override
  public boolean hasEntityLife () {
    return entityLifePlan != null;
  }
  
  
  @Override
  public EntityLife getEntityLife (Object instance) {
    if (entityLifePlan == null) {
      return null;
    } else {
      return entityLifePlan.getFieldValue(instance);
    }
  }
  
  @Override
  public void setEntityLife (Object instance, EntityLife life) {
    if (entityLifePlan != null) {
      entityLifePlan.setFieldValue(instance, life);
    }
  }
  
  
  @Override
  public IItemPlan<?>[] getKeyItems (int index) {
    return uniqueConstraints.get(index);
  }
  

//  @Override
//  public List<INodePlan> getDataNodes (int index) {
//    IItemPlan<?>[] keys = getKeyItems(index);
//    IItemPlan<?> idNode = getIdPlan();
//    IItemPlan<?> versionNode = getVersionPlan();
//    IItemPlan<?> entityLifeNode = getEntityLifePlan();
//    
//    List<INodePlan> dataNodes = new ArrayList<>();
//    for (INodePlan node : getMemberPlans()) {
//      boolean isKey = false;
//      for (IItemPlan<?> key : keys) {
//        if (key.equals(node)) {
//          isKey = true;
//          break;
//        }
//      }
//      if (isKey) {
//        continue;
//      }
//      if (node.equals(idNode)) {
//        continue;
//      }
//      if (node.equals(versionNode)) {
//        continue;
//      }
//      if (node.equals(entityLifeNode)) {
//        continue;
//      }
//      dataNodes.add(node);
//    }
//    return dataNodes;
//  }
  
  
//  @Override
//  public List<IFieldPlan[]> getUniqueConstraints () {
//    return indexes;
//  }
  
  
//  private void buildEntityFields () {
//    List<IItemPlan<?>> memberPlans = new ArrayList<>();
//    getAllFieldPlans(this, memberPlans);
//    
//    List<IItemPlan<?>> keyPlans2 = new ArrayList<>();
//    NaturalKey keyAnn = entityClass.getAnnotation(NaturalKey.class);
//    if (keyAnn != null) {
//      for (String keyName : keyAnn.value()) {
//        IItemPlan<?> plan = getFieldPlan(memberPlans, keyName);
//        keyPlans2.add(plan);
//      }
//      keyPlans = keyPlans2.toArray(new IItemPlan[keyPlans2.size()]);
//    }
//
//    List<IItemPlan<?>> dataPlans2 = getDataPlans(memberPlans, keyPlans2);
//    if (keyPlans == null) {
//      keyPlans = new IItemPlan[1];
//      keyPlans[0] = dataPlans2.get(0);
//      dataPlans2.remove(0);
//    }
//    dataPlans = dataPlans2.toArray(new IItemPlan[dataPlans2.size()]);
//  }
//
//  
//  private static IItemPlan<?> getItemPlan (List<IItemPlan<?>> itemPlans, String name) {
//    for (IItemPlan<?> plan : itemPlans) {
//      if (plan.getName().equals(name)) {
//        return plan;
//      }
//    }
//    throw new IllegalArgumentException(name);
//  }
//  
//  
//  private static void getAllItemPlans (IClassPlan<?> parent, List<IItemPlan<?>> fieldPlans) {
//    for (INodePlan plan : parent.getMemberPlans()) {
//      switch (plan.kind()) {
//      case ITEM :
//        fieldPlans.add((IItemPlan<?>)plan);
//        break;
//      case EMBEDDED :
//        getAllFieldPlans((IClassPlan<?>)plan, fieldPlans);
//        break;
//      default :
//        break;
//      }
//    }
//  }
  
  
  @SuppressWarnings("unchecked")
  private void findEntityItems () {
    IItemPlan<Integer> idPlan2 = null;
    IItemPlan<VersionTime> versionPlan2 = null;
    dataPlans = new ArrayList<>();
    
    INodePlan[] memberPlans = getMembers();
    for (INodePlan member : memberPlans) {
      if (member.isItem()) {
        IItemPlan<?> itemPlan = (IItemPlan<?>)member;
        Id idann = itemPlan.getAnnotation(Id.class);
        if (idann != null) {
          idPlan = (IItemPlan<Integer>)itemPlan;
          // Id fields are not key, data columns
          continue;
        }
        String name = itemPlan.getName();
        if (name.equals("id")) {
          idPlan2 = (IItemPlan<Integer>)itemPlan;
          continue;
        }

        IType<?> type = itemPlan.getType();

        Version vann = itemPlan.getAnnotation(Version.class);
        if (vann != null) {
          versionPlan = (IItemPlan<VersionTime>)itemPlan;
          // Version fields are not key or data columns
          continue;
        }
        if (type instanceof VersionTimeType) {
          versionPlan2 = (IItemPlan<VersionTime>)itemPlan;
          continue;
        }
        
        if (type instanceof EntityLifeType) {
          // Entity life fields are not key or data columns
          entityLifePlan = (IItemPlan<EntityLife>)itemPlan;
          continue;
        }
        
        if (itemPlan.isAnnotated(Describing.class)) {
          describingItemPlans.add(itemPlan);
        }

        if (itemPlan.isAnnotated(PrimaryKey.class) || itemPlan.isAnnotated(SecondaryKey.class) ||
            itemPlan.isAnnotated(Identifiable.class)) {
          identifyingItemPlans.add(itemPlan);
        }
        
        if (firstItemPlan == null) {
          firstItemPlan = itemPlan;
        }
      }
      dataPlans.add(member);
    }
    if (idPlan == null) {
      idPlan = idPlan2;
    }
    if (versionPlan == null) {
      versionPlan = versionPlan2;
    }
 }


  @Override
  public List<IItemPlan<?>[]> getUniqueConstraints() {
    return uniqueConstraints;
  }
  
  
  @Override
  public List<INodePlan> getDataPlans() {
    return dataPlans;
  }
  
  
  private void findUniqueConstraints() {
    Class<?> entityClass = aclass.getSourceClass();
    UniqueConstraint[] ucAnnx = entityClass.getAnnotationsByType(UniqueConstraint.class);
    uniqueConstraints = new ArrayList<>(ucAnnx.length);
    for (UniqueConstraint ucAnn : ucAnnx) {
      IItemPlan<?>[] fields = new IItemPlan[ucAnn.value().length];
      int i = 0;
      for (String name : ucAnn.value()) {
        INodePlan keyNode = getMember(name);
        if (keyNode == null) {
          throw new IllegalArgumentException("Item '" + name + "' in unique constraint on class '" + entityClass.getName() + "' does not exist");
        }
        if (!keyNode.isItem()) {
          throw new IllegalArgumentException("Node '" + name + "' in unique constraint on class '" + entityClass.getName() + "' must be an item");
        }
        fields[i] = (ItemPlan<?>)keyNode;
        i++;
      }
      uniqueConstraints.add(fields);
    }
  }

  
  @Override
  public String getDescription(Object instance) {
    if (instance instanceof IExplicitDescription) {
      return ((IExplicitDescription)instance).getDescription();
    }
    String description = "";
    int i = 0;
    for (IItemPlan<?> itemPlan : describingItemPlans) {
      if (i == 0) {
        description = itemPlan.getDisplayString(instance);
      } else {
        description += ", " + itemPlan.getDisplayString(instance);
      }
      i++;
    }
    return description;
  }

  
//  @Override
//  public List<IItemPlan<?>> getDescriptionPlans () {
//    return descriptionPlans;
//  }
  
  
//  private void findDescriptionItems () {
//    descriptionPlans = new ArrayList<>();
//
//    // Use the entity's self describing method if present
//    if (SelfDescribing.class.isAssignableFrom(entityClass)) {
//      FieldDependency fieldDependency = new FieldDependency();
//      fieldDependency.parseClass(entityClass);
//      List<String> fieldNames = fieldDependency.getDependencies(entityClass.getName(), "invokeDescription");
//      for (String fieldName : fieldNames) {
//        IItemPlan<?> itemPlan = (IItemPlan<?>)getMember(fieldName);
//        descriptionPlans.add(itemPlan);
//      }
//      return;
//    }
//    
//    // Otherwise, concatenate all top level nodes that are marked as describing.
//    for (INodePlan nodePlan : getMembers()) {
//      if  (nodePlan.getStructure() == PlanStructure.ITEM) {
//        IItemPlan<?> itemPlan = (IItemPlan<?>)nodePlan;
//        if (itemPlan.isDescribing()) {
//          descriptionPlans.add(itemPlan);
//        }
//      }
//    }
//    if (descriptionPlans.size() > 0) {
//      return;
//    }
//    
//    // Otherwise, use the first top level String node
//    for (INodePlan nodePlan : getMembers()) {
//      if  (nodePlan.getStructure() == PlanStructure.ITEM) {
//        IItemPlan<?> itemPlan = (IItemPlan<?>)nodePlan;
//        IType<?> type = itemPlan.getType();
//        if (type instanceof StringType) {
//          descriptionPlans.add(itemPlan);
//          return;
//        }
//      }
//    }
//    
//    // Otherwise, leave the list empty
//  }

  @Override
  public void indent (int level) {
    for (int i = 0; i < level; i++) {
      System.out.print("  ");
    }
  }
  
  
  @Override
  public void dump (int level) {
    indent(level);
    System.out.println("EntityPlan " + getEntityName() + ":");
    //for (IRuntimeFactoryProvider factoryProvider : runtimeFactoryProviders) {
    //  indent(level + 1);
    //  System.out.println(factoryProvider);
    //}
    super.dump(level + 1);
  }


  @Override
  public boolean isNullable() {
    return false;
  }


  @SuppressWarnings("unchecked")
  @Override
  public T newInstance() {
    return aclass.newInstance();
  }

  
//  @Override
//  public T newInstance(IItemPlan<?>[] sqlPlans, IResultSet rs) {
//    T instance = newInstance();
//    for (IItemPlan<?> itemPlan : sqlPlans) {
//      itemPlan.setInstanceFromResult(instance, rs);
//    }
//    return instance;
//  }

  
  @Override
  public PlanStructure getStructure () {
    return PlanStructure.ENTITY;
  }


  @SuppressWarnings("unchecked")
  @Override
  public <X> X replicate(X fromValue) {
    return (X)aclass.replicate(fromValue);
  }
  
}
