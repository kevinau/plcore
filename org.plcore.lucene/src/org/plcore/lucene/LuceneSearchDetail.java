package org.plcore.lucene;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
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
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneSearchDetail implements AutoCloseable {

  private final Logger logger = LoggerFactory.getLogger(LuceneSearchDetail.class);

  private IQueryParser queryParser;
  
  private IndexWriter iwriter;
  
  private Timer commitTimer = new Timer("Lucene-commit");
  
  private TimerTask commitTask = null;
  
  private Directory directory;
  
  private DirectoryReader ireader;
  

  public LuceneSearchDetail(Path luceneDir, String name, IQueryParser queryParser) {
    try {
      this.queryParser = queryParser;
      
      directory = FSDirectory.open(luceneDir.resolve(name));
      logger.info("Lucene index: {}", directory);

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
  
  
  @Override
  public void close() {
    try {
      iwriter.close();
      directory.close();
      if (commitTask != null) {
        commitTask.cancel();
        commitTimer.purge();
      }
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

  
  public void addDocument(String id, Document indexDoc) {
    logger.info("Adding document with id: {}", id);
    indexDoc.add(new Field("id", id, StringField.TYPE_STORED));
    try {
      iwriter.addDocument(indexDoc);
      scheduleCommit();
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

    
  public void addDocument(List<IndexableField> document) {
    logger.info("Adding document with {} fields", document.size());
    try {
      iwriter.addDocument(document);
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

      System.out.println(">>>> " + queryString);
      Query query = queryParser.parse(queryString);
      System.out.println(">>>> " + query);
      ScoreDoc[] hits = isearcher.search(query, n).scoreDocs;
      System.out.println(">>>> " + hits.length);
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
