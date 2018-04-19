package org.plcore.dao;


public interface IDataStore {

  public <T> IDataAccessObject<T> getDataAccessObject();
  
}
