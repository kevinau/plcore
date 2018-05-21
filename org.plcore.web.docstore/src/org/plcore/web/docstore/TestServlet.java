package org.plcore.web.docstore;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.plcore.http.Context;
import org.plcore.http.HttpUtility;
import org.plcore.http.Resource;
import org.plcore.template.ITemplate;
import org.plcore.template.ITemplateEngine;
import org.plcore.template.ITemplateEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;


@Context("/pdf")
@Resource(path = "/resources", location = "resources")
@Component(service = HttpHandler.class, immediate = true, configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class TestServlet implements HttpHandler {

  private static Logger logger = LoggerFactory.getLogger(TestServlet.class);

  private ITemplateEngine templateEngine;
  private ITemplate template = null;
  
  private ITemplateEngineFactory templateEngineFactory;

  @Reference
  public void setTemplateEngineFactory (ITemplateEngineFactory templateEngineFactory) {
    this.templateEngineFactory = templateEngineFactory;
  }
  
  
  public void unsetTemplateEngineFactory (ITemplateEngineFactory templateEngineFactory) {
    this.templateEngineFactory = null;
  }
  
  
  @Activate
  public void activate(BundleContext bundleContext) {
    templateEngine = templateEngineFactory.buildTemplateEngine(bundleContext);
  }

  
  @Deactivate
  public void deactivate() {
    this.templateEngine = null;
  }


  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    if (exchange.isInIoThread()) {
      exchange.dispatch(this);
      return;
    }
    logger.info("Handle request: {}", exchange.getRequestPath());
    
    String id = exchange.getRelativePath();
    if (id == null || id.length() <= 1) {
      HttpUtility.endWithStatus(exchange, 400, "Document id not specified as part of request");
      return;
    }
    // Remove leading slash (/)
    id = id.substring(1);
    
//    SourceDocument doc = docStore.getDocument(id);
//    if (doc == null) {
//      HttpUtility.endWithStatus(exchange, 404, "No document found for '" + id + "'");
//      return;
//    }
    
    // Lazily create template
    if (template == null) {
      template = templateEngine.getTemplate("pdfView");
    }

    Map<String, Object> context = new HashMap<>();
    
    exchange.startBlocking();
    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");    
    Writer responseWriter = new OutputStreamWriter(exchange.getOutputStream());
    template.evaluate(responseWriter, context);
  }

}
