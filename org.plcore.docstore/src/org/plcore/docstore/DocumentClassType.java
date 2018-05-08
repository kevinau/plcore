package org.plcore.docstore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.plcore.type.IType;
import org.plcore.type.builtin.CodeType;


//@Component(service = {IType.class, DocumentClassType.class})
public class DocumentClassType extends CodeType<DocumentClass> {

  @Reference(policy = ReferencePolicy.DYNAMIC)
  private final List<ILocalizedAddress> DocumentFamilyAddresses = new CopyOnWriteArrayList<>();

  
  @Override
  protected List<DocumentClass> getValues () {
    List<DocumentClass> values = new ArrayList<>(DocumentFamilyAddresses.size());
    values.add(DocumentClass.UNCLASSIFIED);
    for (ILocalizedAddress address : DocumentFamilyAddresses) {
      values.add(address.getDocumentFamily());
    }
    return values;
  }
  
}
