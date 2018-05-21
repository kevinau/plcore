package org.plcore.lucene;

import org.apache.lucene.search.Query;

public interface IQueryParser {

  public Query parse(String queryText) throws QueryParseException;

}
