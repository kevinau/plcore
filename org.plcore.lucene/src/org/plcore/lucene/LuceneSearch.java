package org.plcore.lucene;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.plcore.home.IApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(service = LuceneSearch.class)
public class LuceneSearch {

  private final Logger logger = LoggerFactory.getLogger(LuceneSearch.class);

  private static final String LUCENE = "lucene";

  @Reference
  private IApplication application;
  
  @Reference
  private IQueryParser queryParser;
    
  private Path luceneDir;
  
  private IndexWriter iwriter;
  
  private Timer commitTimer = new Timer("Lucene-commit");
  
  private TimerTask commitTask = null;
  
  private Directory directory;
  
  private DirectoryReader ireader;
  
  @Activate 
  public void activate(ComponentContext context) {
    try {
      Path baseDir = application.getBaseDir();
      luceneDir = baseDir.resolve(LUCENE);
      Files.createDirectories(luceneDir);

      directory = FSDirectory.open(luceneDir);

      Analyzer analyzer = new StandardAnalyzer();
      IndexWriterConfig config = new IndexWriterConfig(analyzer);
      config.setOpenMode(OpenMode.CREATE_OR_APPEND);
      iwriter = new IndexWriter(directory, config);
      
      // The following is only done on start up
      logger.info("Forcing merge...");
      iwriter.forceMerge(1, true);
      iwriter.forceMergeDeletes(true);
      iwriter.commit();
      logger.info("Merge complete");
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }
  
  
  @Deactivate
  public void deactivate() {
    try {
      iwriter.close();
      directory.close();
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }


  private void scheduleCommit () {
    if (commitTask != null) {
      commitTask.cancel();
      commitTimer.purge();
    }
    commitTask = new TimerTask() {
      @Override
      public void run() {
        try {
          logger.info("Committing Lucene writer");
          iwriter.commit();
        } catch (IOException ex) {
          throw new UncheckedIOException(ex);
        }
      }
    };
    commitTimer.schedule(commitTask, 5000L);
  }

  
  public void addDocument(String id, org.apache.lucene.document.Document indexDoc) {
    logger.info("Adding document with id: {}", id);
    indexDoc.add(new Field("id", id, StringField.TYPE_STORED));
    try {
      iwriter.addDocument(indexDoc);
      scheduleCommit();
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

    
  public void remove(String id) {
    try {
      logger.info("Removing document with id: {}", id);
      iwriter.deleteDocuments(new Term("id", id));
      scheduleCommit();
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  
  public void searchIndex(String queryString, Consumer<Document> consumer) throws QueryParseException {
    searchIndex(queryString, consumer, 100);
  }
  
  
  public void searchIndex(String queryString, Consumer<Document> consumer, int n) throws QueryParseException {
    try {
      if (ireader == null) {
        ireader = DirectoryReader.open(iwriter, true, true);
      } else {
        ireader = DirectoryReader.openIfChanged(ireader, iwriter, true);
      }
      
      IndexSearcher isearcher = new IndexSearcher(ireader);

      Query query = queryParser.parse(queryString);
      ScoreDoc[] hits = isearcher.search(query, n).scoreDocs;
      // Iterate through the results:
      for (int i = 0; i < hits.length; i++) {
        Document hitDoc = isearcher.doc(hits[i].doc);
        consumer.accept(hitDoc);
      }
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

}
