package org.plcore.lucene;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.plcore.entity.Identifiable;
import org.plcore.home.IApplication;
import org.plcore.math.Decimal;
import org.plcore.todo.NotYetImplementedException;
import org.plcore.type.IType;
import org.plcore.type.builtin.DecimalType;
import org.plcore.type.builtin.IntegerType;
import org.plcore.type.builtin.LongType;
import org.plcore.type.builtin.PrimitiveIntType;
import org.plcore.type.builtin.PrimitiveLongType;
import org.plcore.type.builtin.StringBasedType;
import org.plcore.userio.plan.IEntityPlan;
import org.plcore.userio.plan.IItemPlan;
import org.plcore.userio.plan.IPlanFactory;
import org.plcore.util.SimpleCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.persist.model.SecondaryKey;


@Component(property = EventConstants.EVENT_TOPIC + "=" + "org/plcore/dao/DataAccessObject/*")
public class EntityDocumentUpdate implements EventHandler {
  
  private final Logger logger = LoggerFactory.getLogger(EntityDocumentUpdate.class);

  @Reference
  private IPlanFactory planFactory;
  
  @Reference
  private IApplication application;
  
  @Reference
  private IQueryParser queryParser;
    
  private Path luceneDir;
  
  private SimpleCache<LuceneSearchDetail> luceneCache = new SimpleCache<>();
  
  
  @Activate
  private void activate () {
    Path baseDir = application.getBaseDir();
    luceneDir = baseDir.resolve("lucene");
    
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        System.out.println("@@@@@@@@@@@@@@@@@@@@ START");
        LuceneSearchDetail lucene = luceneCache.get("ASXCompany");
        lucene.searchIndex("resources", x -> {
          String className = x.get("$className");
          IEntityPlan<?> entityPlan = planFactory.getEntityPlan(className);
          entityPlan.getIdPlan();
          
          System.out.println("@@@@@@@@@@@@@@@@@@@@ " + className + ":  " + x);
        });
        System.out.println("@@@@@@@@@@@@@@@@@@@@ END");
      }
    }, 20*1000);
  }

  
  @Override
  public void handleEvent(Event event) {
    System.out.println("Event seen:");
    for (String propName : event.getPropertyNames()) {
      Object propValue = event.getProperty(propName);
      System.out.println("  " + propName + ": " + propValue);
    }
    String name = (String)event.getProperty("name");
    Object value = event.getProperty("value");
    
    switch (event.getTopic()) {
    case "org/plcore/dao/DataAccessObject/ADDED" :
      logger.info(":::::::::::: Entity ADDED: " + name + ": " + value);
      try {
        Class<?> klass = value.getClass();
        IEntityPlan<?> entityPlan = planFactory.getEntityPlan(klass);
        IItemPlan<?> idPlan = entityPlan.getIdPlan();
        if (idPlan != null) {
          List<IndexableField> document = new ArrayList<>();
          document.add(new StringField("$className", klass.getName(), Store.YES));

          Object id = idPlan.getFieldValue(value);
          document.add(buildField(idPlan, idPlan.getName(), id, true));

          entityPlan.walkItems(value, (node, v) -> {
            if (node.isAnnotated(Identifiable.class) || node.isAnnotated(SecondaryKey.class)) {
              document.add(buildField(node, "identifying", v, false));
            }
            document.add(buildField(node, node.getName(), v, false));
          });
          
          LuceneSearchDetail lucene = luceneCache.get(name, () -> {
            return new LuceneSearchDetail(luceneDir, name, queryParser);
          });
          lucene.addDocument(document);
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      break;
    case "org/plcore/dao/DataAccessObject/CHANGED" :
      logger.info("Entity CHANGED");
      break;
    case "org/plcore/dao/DataAccessObject/REMOVED" :
      logger.info("Entity REMOVED");
      break;
    }
  }

  
  private IndexableField buildField (IItemPlan<?> itemPlan, String fieldName, Object value, boolean store) {
    IndexableField field;
    
    IType<?> type = itemPlan.getType();
    if (type instanceof StringBasedType) {
      field = new TextField(fieldName, ((String)value).toLowerCase(), store ? Store.YES : Store.NO);
    } else {
      if (store) {
        if (type instanceof IntegerType || type instanceof PrimitiveIntType) {
          field = new StoredField(fieldName, (int)value);
        } else if (type instanceof LongType || type instanceof PrimitiveLongType) {
          field = new StoredField(fieldName, (long)value);
        } else if (type instanceof DecimalType) {
          field = new StoredField(fieldName, ((Decimal)value).toString());
        } else {
          throw new NotYetImplementedException(type.toString());
        }
      } else {
        if (type instanceof IntegerType || type instanceof PrimitiveIntType) {
          field = new IntPoint(fieldName, (int)value);
        } else if (type instanceof LongType || type instanceof PrimitiveLongType) {
          field = new LongPoint(fieldName, (long)value);
        } else if (type instanceof DecimalType) {
          field = new StringField(fieldName, ((Decimal)value).toString(), Store.YES);
        } else {
          throw new NotYetImplementedException(type.toString());
        }
      }
    }
    return field;
  }
  
}