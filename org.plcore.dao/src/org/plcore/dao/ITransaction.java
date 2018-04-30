package org.plcore.dao;


public interface ITransaction<T> extends AutoCloseable {

  public T add(T value);

  public void remove(T value);
  
  public void abort();
  
  @Override
  public void close();
  
}
