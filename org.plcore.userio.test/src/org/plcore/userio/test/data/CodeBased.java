package org.plcore.userio.test.data;

import org.plcore.entity.EntityLife;
import org.plcore.userio.IOField;
import org.plcore.value.ICode;


public class CodeBased {

  public enum Gender {
    MALE, FEMALE, UNKNOWN;
  }

  public enum Weekday implements ICode<Weekday> {

    MON("mon", "Monday"),
    TUE("tue", "Tuesday"),
    WED("wed", "Wednesday"),
    THU("thu", "Thursday"),
    FRI("fri", "Friday"),
    SAT("sat", "Saturday"),
    SUN("sun", "Sunday");

    private final String code;
    private final String description;
    
    private Weekday(String code, String description) {
      this.code = code;
      this.description = description;
    }

    @Override
    public String getCode() {
      return code;
    }

    @Override
    public String getDescription() {
      return description;
    }
    
  }


  @IOField
  public Boolean boolean1;

  @IOField
  public Gender gender;

  @IOField
  public EntityLife entityLife;

  @IOField
  public Weekday weekday;

  // public IImageCodeValue imageCode;

}
