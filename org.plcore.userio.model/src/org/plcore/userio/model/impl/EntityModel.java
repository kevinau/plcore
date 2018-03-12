package org.plcore.userio.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.plcore.userio.model.EntityCreationListener;
import org.plcore.userio.model.IEntityModel;
import org.plcore.userio.model.INodeModel;
import org.plcore.userio.model.ModelFactory;
import org.plcore.userio.model.ref.IValueReference;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.INodePlan;
import org.plcore.value.EntityLife;
import org.plcore.value.VersionTime;

public class EntityModel extends NameMappedModel implements IEntityModel {
  
  private final IValueReference valueRef;
  
  private final IEntityPlan<?> entityPlan;

  private final List<EntityCreationListener> entityCreationListeners = new ArrayList<>();

  private int entityId;
  
  private VersionTime versionTime;
  
  private EntityLife entityLife;
  
  
  public EntityModel (ModelFactory modelFactory, IValueReference valueRef, IEntityPlan<?> entityPlan) {
    super (modelFactory, valueRef, entityPlan);
    this.valueRef = valueRef;
    this.entityPlan = entityPlan;
  }
  
  
  @Override
  public void addEntityCreationListener(EntityCreationListener x) {
    entityCreationListeners.add(x);
    x.entityCreated(this);
  }

  @Override
  public void setValue (Object value) {
    valueRef.setValue(value);
    syncValue(value);
  }
  
  
  @Override
  public void setEntityId (int id) {
    this.entityId = id;
  }
  
  
  @Override
  public void setVersionTime (VersionTime versionTime) {
    this.versionTime = versionTime;
  }
  
  
  @Override
  public void setEntityLife (EntityLife entityLife) {
    this.entityLife = entityLife;
  }
  
  
  @Override
  public <T> T getValue () {
    return valueRef.getValue();
  }
  
  
  @Override
  public void buildQualifiedNamePart (StringBuilder builder, boolean[] isFirst, int[] repeatCount) {
    builder.append(((IEntityPlan<?>)getPlan()).getClassName());
    builder.append('#');
    isFirst[0] = true;
  }
  
  
  @Override
  public <X> X newInstance() {
    return entityPlan.newInstance();
  }
  

  @Override
  public void dump(int level) {
    indent (level);
    System.out.println("EntityModel {");
    super.dump(level + 1);
    indent (level);
    System.out.println("}");
  }

  
  @Override
  public int getEntityId() {
    return entityId;
  }

  
  @Override
  public VersionTime getVersionTime() {
    return versionTime;
  }

  
  @Override
  public EntityLife getEntityLife() {
    return entityLife;
  }

  
  @Override
  public List<INodeModel> getDataModels() {
    List<INodePlan> dataPlans = entityPlan.getDataPlans();
    List<INodeModel> dataModels = new ArrayList<>(dataPlans.size());
    
    for (INodePlan dataPlan : dataPlans) {
      INodeModel dataModel = getMember(dataPlan.getName());
      dataModels.add(dataModel);  
    }
    return dataModels;
  }
  
  
  @Override
  public void destroy () {
    for (EntityCreationListener x : entityCreationListeners) {
      x.entityDestoryed(this);
    }
    entityCreationListeners.clear();
  }
  
  
  @Override
  public IEntityModel getParentEntity() {
    return this;
  }
  
  
  @Override
  public void buildQName (StringBuilder builder) {
    System.out.println("Entity model: buildQName: " + builder.toString());
  }
 
}
