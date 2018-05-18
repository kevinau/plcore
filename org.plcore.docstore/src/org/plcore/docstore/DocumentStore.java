package org.plcore.docstore;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.plcore.dao.IDataAccessObject;
import org.plcore.docstore.parser.IImageParser;
import org.plcore.docstore.parser.IPDFParser;
import org.plcore.docstore.parser.impl.ImageIO;
import org.plcore.docstore.parser.impl.TesseractImageOCR;
import org.plcore.home.IApplication;
import org.plcore.inbox.IFileProcessor;
import org.plcore.nio.SafeOutputStream;
import org.plcore.osgi.Configurable;
import org.plcore.osgi.ConfigurationLoader;
import org.plcore.srcdoc.ISegment;
import org.plcore.srcdoc.ISourceDocumentContents;
import org.plcore.srcdoc.SourceDocument;
import org.plcore.srcdoc.SourceReference;
import org.plcore.util.Digest;
import org.plcore.util.DigestFactory;
import org.plcore.util.MD5DigestFactory;
import org.plcore.util.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(name = "DocumentStore", configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class DocumentStore implements IDocumentStore, IFileProcessor {

  private Logger logger = LoggerFactory.getLogger(DocumentStore.class);
  
  // This document store uses the MD5 digest to identify documents
  private final DigestFactory digestFactory = new MD5DigestFactory();

  @Reference
  private ConfigurationLoader configLoader;
  
  @Reference
  private IApplication application;
  
  @Reference
  private IPDFParser pdfParser;
  
  @Reference(target = "(name=SourceDocument)")
  private IDataAccessObject<SourceDocument> sourceDocDAO;

  @Reference
  private EventAdmin eventAdmin;

  @Configurable
  private Path baseDir = null;

  
  private static final String IMAGES = "images";
  private static final String SOURCE = "source";
  private static final String THUMBS = "thumbs";

  private Path sourceDir;
  private Path imagesDir;
  private Path thumbsDir;
  
  
  public DocumentStore () {
  }
  
  
  @Activate
  public void activate(ComponentContext context) {
    baseDir = application.getBaseDir();
    
    configLoader.load(this, context);

    try {
      sourceDir = baseDir.resolve(SOURCE);
      Files.createDirectories(sourceDir);
      imagesDir = baseDir.resolve(IMAGES);
      Files.createDirectories(imagesDir);
      thumbsDir = baseDir.resolve(THUMBS);
      Files.createDirectories(thumbsDir);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  
  private void fireEvent(SourceDocument doc, String action) {
    Map<String, Object> props = new HashMap<>();
    props.put("document", doc);

    Event event = new Event("org/plcore/docstore/Document/" + action, props);
    eventAdmin.postEvent(event);
  }

  
  @Override 
  public Path getSourcePath (String hashCode, String extn) {
    return sourceDir.resolve(hashCode + extn);
  }

  
  @Override
  public String importDocument(URL url) {
    logger.info("Importing document from URL {}", url);
    String pathName = url.getPath();
    int n = pathName.lastIndexOf('.');
    if (n == -1) {
      throw new IllegalArgumentException("Source URL with no extension (ie no type)");
    }
    String extn = pathName.substring(n).toLowerCase();
    String originName = new File(pathName).getName();
    
    Digest digest = digestFactory.getFileDigest(url);
    String hashCode = digest.toString();
    InputStream is;
    try {
      is = url.openStream();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return importDocument(hashCode, is, originName, extn);
  }
  
  
  @Override
  public String importDocument(File file) {
    return importDocument(file.toPath());
  }
  
  
  @Override
  public String importDocument(Path path) {
    String hashCode = digestFactory.getFileDigest(path).toString();
    return importDocument(path, hashCode);
  }
  
  
  public String importDocument(Path path, String hashCode) {
//    Path catalogPath = catalogDir.resolve(hashCode + ".ser");
//    if (Files.exists(catalogPath)) {
//      logger.info("{} already exists as {}. Not importing", path, hashCode);
//       // No need to import.  The file already exists.  The file is uniquely named by it's hashCode, 
//      // so if it exists under that name, it exists and is current.  Size and time stamp do not 
//      // need to be checked.
//      return hashCode;
//    }
    
    logger.info("Importing {} as {}", path, hashCode);
    String pathName = path.toString();
    int n = pathName.lastIndexOf('.');
    if (n == -1) {
      throw new IllegalArgumentException("Source path with no extension (ie no type)");
    }
    String extn = pathName.substring(n).toLowerCase();
    Path newSourcePath = sourceDir.resolve(hashCode + extn);
    
    // As a double check, check that the file does not exist
    if (!Files.exists(newSourcePath)) {
      // Copy the file
      try (SafeOutputStream targetOutputStream = new SafeOutputStream(newSourcePath)) {
        Files.copy(path, targetOutputStream);
        targetOutputStream.commit();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
    
    Timestamp lastModified = new Timestamp(path.toFile().lastModified());
    completeImport (hashCode, newSourcePath, lastModified, path.getFileName().toString(), extn);

    return hashCode;
  }
  

  @Override
  public String importDocument(InputStream is, MimeType mimeType) {
    Digest digest = digestFactory.getInputStreamDigest(is);
    String hashCode = digest.toString();
    
    try {
      is.reset();
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return importDocument(hashCode, is, null, mimeType.getExtension());
  }
  
  
  private String importDocument(String hashCode, InputStream is, String originName, String extn) {
//    Path catalogPath = catalogDir.resolve(hashCode + ".ser");
//    if (Files.exists(catalogPath)) {
//      // No need to import.  The file already exists.  The file is uniquely named by it's id, 
//      // so if it exists under that name, it exists and is current.  Size and timestamp do not 
//      // need to be checked.
//      return hashCode;
//    }

    Path newSourcePath = sourceDir.resolve(hashCode + extn);
    
    // As a double check, check that the file does not exist
    if (!Files.exists(newSourcePath)) {
      // Copy the file
      try (SafeOutputStream targetOutputStream = new SafeOutputStream(newSourcePath)) {
        byte[] buffer = new byte[4096];
        int n = is.read(buffer);
        while (n > 0) {
          targetOutputStream.write(buffer, 0, n);
          n = is.read(buffer);
        }
        targetOutputStream.commit();
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
    
    completeImport (hashCode, newSourcePath, null, originName, extn);
    return hashCode;
  }

  
  private void completeImport (String hashCode, Path sourcePath, Timestamp originTime, String originName, String extn) {
    Path path = getSourcePath(hashCode, extn);

    logger.info("Parsing {} to extact textual contents", path.getFileName());
    ISourceDocumentContents docContents;
    if (isImageFile(extn)) {
      IImageParser imageParser = new TesseractImageOCR();
      docContents = imageParser.parse(hashCode, 0, path);
      
      // Write a thumbnail version of the image
      BufferedImage image = ImageIO.getImage(path);
      Path thumbsFile = getThumbsImagePath(hashCode);
      ImageIO.writeThumbnail(image, thumbsFile);
    } else {
      switch (extn) {
      case ".pdf" :
        //IImageParser imageParser = new TesseractImageOCR();
        //IPDFParser pdfParser = new PDFBoxPDFParser(imageParser);
        docContents = pdfParser.parse(hashCode, path, IMAGE_RESOLUTION, this);
        break;
      default :
        throw new RuntimeException("File type: " + path + " not supported");
      }
    }

    // Get a viewable image of the document (for overlaying with field information).
    // Image source files do not need a viewable image.
    ////if (!isImageFile(extn)) {
    ////  writeViewImage (id, sourcePath, extn);
    ////}
    
    // Write Document record
    Timestamp importTime = new Timestamp(System.currentTimeMillis());
    SourceDocument document = new SourceDocument(hashCode, originTime, originName, extn, importTime, docContents);
    sourceDocDAO.add(document);
    
    logger.info("Import complete: {} -> {}", originName, hashCode + ".ser");

    fireEvent(document, "ADDED");
    
    // For debugging
//    Document d = primaryIndex.get(id);
//    IDocumentContents dc = d.getContents();
//    System.out.println(">>>> " + dc);
//    for (ISegment seg : dc.getSegments()) {
//      System.out.println(">>>> " + seg);
//    }
  }

  
  private static final String[] imageExtensions = {
      ".png",
      ".jpg",
      ".jpeg",
      ".tiff",
      ".gif",
      ".bmp",
      ".bpg",
      ".svg",
  };

  
  @Override
  public boolean isImageFile (String extn) {
    for (String ie : imageExtensions) {
      if (extn.equals(ie)) {
        return true;
      }
    }
    return false;
  }
  
  
//  private void writeViewImage (String id, Path file, String extn) {
//    switch (extn) {
//    case ".pdf" :
//      PDFToImage.buildImageFile(id, file, this);
//      break;
//    default :
//      throw new RuntimeException("Cannot build view image for " + file + ": not supported");
//    }
//  }
  
  @Override
  public Path getBasePath () {
    return baseDir;
  }
  
  
  @Override 
  public Path getViewImagePath (SourceDocument doc) {
    // If the origin extension is that of an image, the view image path is the source path
    String extn = doc.getOriginExtension();
    if (isImageFile(extn)) {
      return sourceDir.resolve(doc.getHashCode() + extn);
    } else {
      return imagesDir.resolve(doc.getHashCode() + ".png");
    }
  }
  

  @Override
  public String webViewImagePath (String hashCode, String extn, int page) {
    String base = "/" + IMAGES + "/" + hashCode;
    if (page > 0) {
      base += ".p" + page;
    }
    if (isImageFile(extn)) {
      return base + extn;
    } else {
      return base + ".png";
    }
  }

  
  @Override 
  public String webThumbsImagePath (String hashCode) {
    return "/" + THUMBS + "/" + hashCode + ".png";
  }
  

  @Override 
  public String webSourcePath (SourceDocument doc) {
    return "/" + SOURCE + "/" + doc.getHashCode() + doc.getOriginExtension();
  }
  

  @Override 
  public Path newViewImagePath (String hashCode, int page) {
    if (page == 0) {
      return imagesDir.resolve(hashCode + ".png");
    } else {
      return imagesDir.resolve(hashCode + ".p" + page + ".png");
    }
  }
  

  @Override 
  public Path getThumbsImagePath (String hashCode) {
    return thumbsDir.resolve(hashCode + ".png");
  }
  

//  @Override 
//  public Path getViewImagePath (String id, String extn) {
//    if (isImageFile(extn)) {
//      return sourceDir.resolve(id + extn);
//    } else {
//      return imagesDir.resolve(id + ".png");
//    }
//  }
  

//  @Override
//  public void saveViewImage (String id, BufferedImage image) {
//    Path imageFile = getViewImagePath(id);
//    PngEncoder pngEncoder = new PngEncoder(PngEncoder.COLOR_TRUECOLOR);
//    try (FileOutputStream imageOut = new FileOutputStream(imageFile.toFile())) {
//      pngEncoder.encode(image, imageOut);
//    } catch (IOException ex) {
//      throw new RuntimeException(ex);
//    }
//  }


  @Override
  public Path getViewHTMLPath(String hashCode) {
    Path htmlDir = baseDir.resolve("html");
    try {
      Files.createDirectories(htmlDir);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
    return htmlDir.resolve(hashCode + ".html");
  }


  @Override
  public void removeDocument(SourceDocument document) {
    String hashCode = document.getHashCode();
    String extension = document.getOriginExtension();
    removeDocument(hashCode, extension);
    
    fireEvent(document, "REMOVED");
  }


  private void removeDocument(String hashCode, String extension) {
    try {
      // Remove the document from all the 'at-rest' places
//      // Document details and contents
//      Path catalogDir = baseDir.resolve(CATALOG);
//      Path catalogPath = catalogDir.resolve(hashCode + ".ser");
//      Files.deleteIfExists(catalogPath);
      
      // Source file
      Path sourcePath = getSourcePath(hashCode, extension);
      Files.deleteIfExists(sourcePath);
      
      // Thumbnail
      Path thumbPath = getThumbsImagePath(hashCode);
      Files.deleteIfExists(thumbPath);
      
      // All page images
      // TODO need to do this
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }


  @Override
  public SourceDocument getDocument(String hashCode) {
    return sourceDocDAO.getByPrimary(hashCode);
  }


  @Override
  public List<SourceReference> getAllDocuments() {
    List<SourceReference> docList = new ArrayList<>();
    
//    String[] names = catalogDir.toFile().list();
//    for (String name : names) {
//      if (name.endsWith(".ser")) {
//        SourceDocument doc = SourceDocument.load(catalogDir.resolve(name));
//        docList.add(doc);
//      }
//    }
    return docList;
  }


  public void rebuildPDF (String hashCode, int dpi) {
    Path path = getSourcePath(hashCode, ".pdf");
    //IImageParser imageParser = new TesseractImageOCR();
    //IPDFParser pdfParser = new PDFBoxPDFParser(imageParser);
    ISourceDocumentContents docContents = pdfParser.parse(hashCode, path, dpi, this);
    for (ISegment seg : docContents.getSegments()) {
      System.out.println(seg);
    }
  }
  
  
  public static void main (String[] args) {
    DocumentStore docStore = new DocumentStore();
    docStore.rebuildPDF("5dbc-9fbef7c4a0c2", IMAGE_RESOLUTION);
  }


  @Override
  public void process(Path path, String hashCode) {
    this.importDocument(path, hashCode);
  }
  
  @Override
  public void unprocess(Path path, String hashCode) {
    String fileName = path.getFileName().toString();
    int n = fileName.lastIndexOf('.');
    String extension;
    if (n >= 0) {
      extension = fileName.substring(n);
    } else {
      extension = "";
    }
    this.removeDocument(hashCode, extension);
  }
  
}
