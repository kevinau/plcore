package org.plcore.docstore.lucerne;

import java.util.List;


public interface ISearchEngine {

  public List<DocumentReference> searchIndex(String queryString);

}
