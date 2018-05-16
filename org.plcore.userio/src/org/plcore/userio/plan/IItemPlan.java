package org.plcore.userio.plan;

import org.plcore.type.IType;
import org.plcore.userio.INode;


public interface IItemPlan<T> extends INodePlan, INode {

  /** 
   * The type of this input field.
   */
  public IType<T> getType();

  /**
   * Is an empty input field acceptable.  If this is true, an empty input field is acceptable
   * and the resultant field value is <code>null</code>.  If this is false, an empty input field
   * is reported as an error.
   * <p>
   * Note that, it is possible that the input checking of IType does not allow an empty input field.  If
   * this method returns true, the error checking of IType is bypassed and the field value is 
   * set to <code>null</code>.
   * <p>
   * For primitive Java types, this method always returns false.
   */
  @Override
  public boolean isNullable();

  @Override
  public <X> X getFieldValue(Object instance);

//  public T getFieldDefaultValue();
  
  @Override
  public void setFieldValue(Object instance, Object value);
  
  public String getDisplayString(Object instance);
  
  public String getEntrySource(Object instance, T fillValue);
  
  //public boolean isDescribing();
  
//  public T getResultValue(IResultSet rs);
//
//  public default void setStatementFromInstance (IPreparedStatement stmt, Object instance) {
//    T value = getFieldValue(instance);
//    setStatementFromValue (stmt, value);
//  }
//  
//  public void setStatementFromValue (IPreparedStatement stmt, T value);
//
//  public default void setInstanceFromResult (Object instance, IResultSet rs) {
//    T value = getType().getResultValue(rs);
//    setFieldValue (instance, value);
//  }

//  public default void setInstanceFromResult (Object instance, IResultSet rs, int[] i) {
//    setInstanceFromResult (instance, rs, i[0]++);
//  }
  
}
