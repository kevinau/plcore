package org.plcore.userio.plan;

import java.sql.Timestamp;
import java.util.List;

import org.plcore.entity.EntityLife;
import org.plcore.entity.VersionTime;



public interface IEntityPlan<T> extends IEmbeddedPlan<T> {

  public String getEntityName();

  public IItemPlan<Integer> getIdPlan();
  
  public boolean hasId();
  
  public int getId(Object instance);
 
  public void setId(Object instance, int id);

  public List<IItemPlan<?>[]> getUniqueConstraints();

  public IItemPlan<EntityLife> getEntityLifePlan();
  
  public boolean hasEntityLife();
  
  public EntityLife getEntityLife(Object instance);
    
  public void setEntityLife(Object instance, EntityLife life);

  /** 
   * Returns the version field for this entity.  If the entity does not have
   * a version field, <code>null</code> is returned.
   */
  public IItemPlan<VersionTime> getVersionPlan();
  
  public boolean hasVersion ();
  
  public Timestamp getVersion(Object instance);
  
  public void setVersion(Object instance, Timestamp version);

  public IItemPlan<?>[] getKeyItems(int index);
  
  @Override
  public <X> X newInstance();
  
  @Override
  public <X> X replicate(X fromValue);
  
//  public T newInstance(IItemPlan<?>[] sqlPlans, IResultSet rs);
  
  public List<INodePlan> getDataPlans();

  //public List<IItemPlan<?>> getDescriptionPlans();

}
