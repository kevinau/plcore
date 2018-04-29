package org.plcore.userio.model;

import java.util.List;

import org.plcore.userio.IEntityNode;
import org.plcore.value.EntityLife;
import org.plcore.value.VersionTime;

public interface IEntityModel extends IEmbeddedModel, IEntityNode {

  public void setEntityId(int id);
  
  public void setVersionTime(VersionTime versionTime);
  
  public void setEntityLife(EntityLife entityLife);
  
  public int getEntityId();
  
  public VersionTime getVersionTime();
  
  public EntityLife getEntityLife();
  
  public List<INodeModel> getDataModels();

  public void addEntityCreationListener(EntityCreationListener x);
  
  public void destroy();

  public List<ReportableError> getErrors();
  
}
