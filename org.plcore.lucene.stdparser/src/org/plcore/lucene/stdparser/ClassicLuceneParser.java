package org.plcore.lucene.stdparser;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.plcore.lucene.IQueryParser;
import org.plcore.lucene.QueryParseException;
import org.plcore.osgi.ComponentConfiguration;
import org.plcore.osgi.Configurable;

@Component(configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class ClassicLuceneParser implements IQueryParser {

  @Configurable
  private String defaultFieldName = "text";

  private QueryParser queryParser;
  

  @Activate
  private void activate (ComponentContext context) {
    ComponentConfiguration.load(this, context);
    
    StandardAnalyzer analyzer = new StandardAnalyzer();
    queryParser = new QueryParser(defaultFieldName, analyzer);
  }
  
  
  @Override
  public Query parse(String queryText) throws QueryParseException {
    try {
      return queryParser.parse(queryText);
    } catch (ParseException ex) {
      throw new QueryParseException(ex.getMessage());
    }
  }

}
