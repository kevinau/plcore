/*******************************************************************************
 * Copyright (c) 2012 Kevin Holloway (kholloway@geckosoftware.co.uk).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kevin Holloway - initial API and implementation
 *******************************************************************************/
package org.plcore.type.password;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.plcore.osgi.ComponentConfiguration;
import org.plcore.osgi.Configurable;
import org.plcore.todo.NotYetImplementedException;
import org.plcore.type.Type;
import org.plcore.type.UserEntryException;
import org.plcore.value.PasswordValue;



@Component
public class PasswordType extends Type<PasswordValue> {

  private static final String REQUIRED_MESSAGE = "Required";
  
  @Configurable
  private int minimumLength = 8;
  
  @Activate 
  protected void activate(Map<String, Object> props) {
    // TODO remove the print line
    System.out.println("Activating PasswordType");
    ComponentConfiguration.load(this, props);
  }
  
  
  @Override
  public PasswordValue createFromString (String source) throws UserEntryException {
    if (source.length() < minimumLength) {
      throw new UserEntryException("Minimum of " + minimumLength + " characters are required", UserEntryException.Type.INCOMPLETE);
    }
    PasswordValue pv = new PasswordValue(source);
    validate (pv);
    return pv;
  }


  @Override
  public String toEntryString (PasswordValue value, PasswordValue fillValue) {
    return "";
  }
  
  
  @Override
  public PasswordValue primalValue() {
    throw new NotYetImplementedException();
  }


  @Override
  public PasswordValue newInstance(String source) {
    return new PasswordValue(source);
  }


  @Override
  protected void validate(PasswordValue value) throws UserEntryException {
    // No further validation required
  }
  
  
  @Override
  public String getRequiredMessage () {
    return REQUIRED_MESSAGE;
  }


  @Override
  public Class<?> getFieldClass() {
    return PasswordValue.class;
  }


  @Override
  public int getFieldSize() {
    return minimumLength + 10;
  }
  
}
