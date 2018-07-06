package org.pennyledger.data.asx200;

import org.osgi.service.component.annotations.Component;
import org.plcore.entity.IEntity;
import org.plcore.math.Decimal;
import org.plcore.type.NumberSign;
import org.plcore.userio.IOField;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.SecondaryKey;

@Entity
@Component
public class ASXCompany implements IEntity {

  @PrimaryKey(sequence = "ASXCompnay_ID")
  private int id;
  
  @SecondaryKey(relate = Relationship.ONE_TO_ONE)
  @IOField(pattern = "A-Z0-9{3}", length = 3)
  private String code;
  
  private String company;
  
  private String sector;
  
  @IOField(sign = NumberSign.UNSIGNED)
  private long marketCap;
  
  @IOField(precision = 5, scale = 2)
  private Decimal weight;
  
  @Override
  public String toString() {
    return "ASXCompany[" + id + "," + code + "," + company + "," + sector + "," + marketCap + "," + weight +"]";
  }

}
