package org.plcore.docstore;

import org.plcore.value.Code;

public class DocumentClass extends Code<DocumentClass> {

  private static final long serialVersionUID = 1L;

  public static final DocumentClass UNCLASSIFIED = new DocumentClass("", "Unclassified");
  
  public DocumentClass(String code, String description) {
    super(code, description);
  }

}
