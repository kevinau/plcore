package org.plcore.dao.berkeley;

import org.plcore.dao.ITransaction;

import com.sleepycat.je.Transaction;
import com.sleepycat.persist.PrimaryIndex;

public class TransactionSet<T> implements ITransaction<T> {

  private final DataAccessObject<T> dao;
  
  private final Transaction transaction;

  private final PrimaryIndex<Object, T> primaryIndex;
  
  public TransactionSet(DataAccessObject<T> dao, Transaction transaction, PrimaryIndex<Object, T> primaryIndex) {
    this.dao = dao;
    this.transaction = transaction;
    this.primaryIndex = primaryIndex;
  }


  @Override
  public T add(T value) {
    return dao.add(primaryIndex, value);
  }
  
  
  @Override
  public void remove(T value) {
    dao.remove(primaryIndex, value);
  }
  
  
  @Override
  public void abort() {
    transaction.abort();
  }

  
  @Override
  public void close() {
    transaction.commit();
  }

}
