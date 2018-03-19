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
package org.plcore.type.builtin;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import org.plcore.type.UserEntryException;
import org.plcore.type.builtin.StringBasedType;


public class URLType extends StringBasedType<URL> {
  
  public URLType () {
    super (128);
  }


  public URLType (URLType type) {
    super (type);
  }
  
  
  @Override
  public URL createFromString (URL fillValue, boolean creating, String source) throws UserEntryException {
    try {
      String sv = source.trim();
      URL value;
      if (fillValue != null) {
        value = new URL(fillValue, "http://" + sv);
      } else {
        value = new URL("http://" + sv);
      }
      return value;
    } catch (MalformedURLException ex) {
      throw new UserEntryException("invalid internet address (URL without leading http://)", false);
    }
  }


  @Override
  public void validate(URL value) throws UserEntryException {
  }

  
  @Override
  public URL newInstance(String source) {
    try {
      return new URL(source);
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    }
  }


  @Override
  public URL primalValue() {
    try {
      return new URL("http://" + InetAddress.getLocalHost());
    } catch (MalformedURLException ex) {
      throw new RuntimeException(ex);
    } catch (UnknownHostException ex) {
      throw new RuntimeException(ex);
    }
  }


  @Override
  public Class<?> getFieldClass() {
    return URL.class;
  }

}
