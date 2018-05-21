package org.plcore.docstore.parser.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.plcore.docstore.parser.IImageParser;
import org.plcore.docstore.segment.SegmentMatcherList;
import org.plcore.osgi.ComponentConfiguration;
import org.plcore.osgi.Configurable;
import org.plcore.srcdoc.ISourceDocumentContents;
import org.plcore.srcdoc.PartialSegment;
import org.plcore.srcdoc.SourceDocumentContents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


@Component(configurationPolicy = ConfigurationPolicy.REQUIRE)
public class TesseractImageOCR implements IImageParser {

  private Logger logger = LoggerFactory.getLogger(TesseractImageOCR.class);

  @Configurable(required = true)
  private String tesseractHome;
  
  @Activate
  private void activate(ComponentContext context) {
    ComponentConfiguration.load(this, context);
  }
  
//  private ISourceDocumentContents readOCRResults(int pageIndex, Path resultsFile) {
//    XMLInputFactory2 factory = null;
//    try {
//      factory = (XMLInputFactory2)XMLInputFactory2.newInstance();
//      factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
//      factory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
//      factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
//      factory.setProperty(XMLInputFactory.IS_COALESCING, false);
//
//      Reader resultsReader = Files.newBufferedReader(resultsFile);
//      XMLStreamReader reader = factory.createXMLStreamReader(resultsReader);
//
//      SourceDocumentContents docContents = new SourceDocumentContents();
//      PartialSegment lineSegment = null;
//      PartialSegment wordSegment = null;
//      int level = 0;
//      int lineLevel = 0;
//
//      while (reader.hasNext()) {
//        int event = reader.next();
//        switch (event) {
//        case XMLStreamConstants.START_DOCUMENT :
//          break;
//        case XMLStreamConstants.START_ELEMENT :
//          level++;
//          String klass = reader.getAttributeValue(null, "class");
//          if (klass != null) {
//            switch (klass) {
//            case "ocr_line" :
//              lineSegment = null;
//              lineLevel = level;
//              break;
//            case "ocrx_word" :
//              // Create an empty segment with the word's bounding box. Attribute
//              // "title" contains the bounding box dimensions.
//              wordSegment = new PartialSegment(pageIndex, reader.getAttributeValue(null, "title"), null);
//              break;
//            }
//          }
//          break;
//        case XMLStreamConstants.CHARACTERS :
//          String word = reader.getText().trim();
//          if (word.length() != 0) {
//            if (lineSegment != null && lineSegment.overlaps(wordSegment, word)) {
//              if (lineSegment.almostAdjacent(wordSegment, word)) {
//                // If the two segments are almost adjacent, append the text
//                // without a space
//                lineSegment.extendWide(wordSegment, word);
//              } else {
//                // ... otherwise add a space between the two segments
//                lineSegment.extendWide(wordSegment, ' ', word);
//              }
//            } else {
//              // Add the existing, completed, line segment to the document
//              // before starting a new one.
//              if (lineSegment != null) {
//                docContents.add(SegmentMatcherList.matchers, lineSegment);
//                // logger.info("Adding line segment:{}", lineSegment);
//              }
//              lineSegment = new PartialSegment(wordSegment, word, 0);
//            }
//          }
//          break;
//        case XMLStreamConstants.END_ELEMENT :
//          if (lineLevel == level && lineSegment != null) {
//            // logger.info("Adding line segmnt: {}", lineSegment);
//            docContents.add(SegmentMatcherList.matchers, lineSegment);
//          }
//          level--;
//          break;
//        }
//      }
//      reader.close();
//      resultsReader.close();
//
//      return docContents;
//    } catch (FactoryConfigurationError | XMLStreamException | IOException ex) {
//      throw new RuntimeException(ex);
//    }
//  }

  private static final double ADJACENT_TOLERANCE = 0.5;
  private static final double SPACE_TOLERANCE = 1.2;
  
  private static Pattern titlePattern = Pattern.compile("[ ;]");


  private void parseOcrLine(SourceDocumentContents docContents, Document doc, int page, Element e) {
    PartialSegment psegment = null;
    
    final NodeList children = e.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      final Node n = children.item(i);
      if (n.getNodeType() == Node.ELEMENT_NODE) {
        Element ce = (Element)n;
        String classAttribute = ce.getAttribute("class");
        if (classAttribute.equals("ocrx_word")) {
          String titleAttrib = ce.getAttribute("title");
          String[] x = titlePattern.split(titleAttrib);
          float nextx0 = Float.parseFloat(x[1]);
          float nexty0 = Float.parseFloat(x[2]);
          float nextx1 = Float.parseFloat(x[3]);
          float nexty1 = Float.parseFloat(x[4]);

          String text = ce.getTextContent();
          if (psegment == null) {
            psegment = new PartialSegment(page, nextx0, nexty0, nextx1, nexty1, text);
          } else {
            double aveCharWidth = psegment.aveCharWidth(nextx1 - nextx0, text);
            double gap = nextx0 - psegment.getX1();
            
            if (gap < aveCharWidth * ADJACENT_TOLERANCE) {
              psegment.extendWide(nextx0, nexty0, nextx1, nexty1, text);
            } else if (gap < aveCharWidth * SPACE_TOLERANCE) {
              psegment.extendWideWithSpace(nextx0, nexty0, nextx1, nexty1, text);
            } else {
              // Add currently extended segment
              docContents.add(SegmentMatcherList.matchers, psegment);
              psegment = new PartialSegment(page, nextx0, nexty0, nextx1, nexty1, text);
            }
          }
        } else {
          parseOcrLine(docContents, doc, page, ce);
        }
      }
    }
    if (psegment != null) {
      docContents.add(SegmentMatcherList.matchers, psegment);
    }
  }


  private void parseOuter(SourceDocumentContents docContents, Document doc, int page, Element e) {
    final NodeList children = e.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      final Node n = children.item(i);
      if (n.getNodeType() == Node.ELEMENT_NODE) {
        Element ce = (Element)n;
        String classAttribute = ce.getAttribute("class");
        if (classAttribute.equals("ocr_line")) {
          parseOcrLine (docContents, doc, page, ce);
        } else {
          parseOuter (docContents, doc, page, ce);
        }
      }
    }
  }


  private ISourceDocumentContents readOCRResults2(int pageIndex, Path resultsFile) {
    SourceDocumentContents docContents = new SourceDocumentContents();

    Document doc;
    try {
      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
      docBuilderFactory.setValidating(false);
      docBuilderFactory.setNamespaceAware(true);
      docBuilderFactory.setFeature("http://xml.org/sax/features/namespaces", false);
      docBuilderFactory.setFeature("http://xml.org/sax/features/validation", false);
      docBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
      docBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

      DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
      doc = docBuilder.parse(resultsFile.toFile());
    } catch (ParserConfigurationException | SAXException | IOException ex) {
      throw new RuntimeException(ex);
    }
    parseOuter(docContents, doc, pageIndex, doc.getDocumentElement());

    return docContents;
  }
    
  
  @Override
  public ISourceDocumentContents parse(String id, int pageIndex, Path imagePath) {
    logger.info("Starting Tesseract OCR of: {}, page {}", imagePath, pageIndex);

    Path ocrBase = OCRPaths.getBasePath(id);

    String[] cmd = { tesseractHome + "/tesseract", "--oem", "2", imagePath.toString(), ocrBase.toString(), "hocr" };
    // logger.info("Starting Tesseract OCR: " + cmd[0] + "|" + cmd[1] + "|" +
    // cmd[2] + "|" + cmd[3] + "|" + cmd[4]);
    for (String cx : cmd) {
      System.out.print(cx + " ");
    }
    System.out.println();

    Runtime runtime = Runtime.getRuntime();
    try {
      String[] argp = {
          "TESSDATA_PREFIX=" + tesseractHome,
      };
      Process process = runtime.exec(cmd, argp);
      int result = process.waitFor();
      System.out.println("Result: " + result);
      InputStream outputStream = process.getInputStream();
      byte[] buffer = new byte[1024];
      int n = outputStream.read(buffer);
      while (n > 0) {
        System.err.print(new String(buffer, 0, n));
        n = outputStream.read(buffer);
      }
      if (result != 0) {
        InputStream errorStream = process.getErrorStream();
        buffer = new byte[1024];
        n = errorStream.read(buffer);
        while (n > 0) {
          System.err.print(new String(buffer, 0, n));
          n = errorStream.read(buffer);
        }
        throw new RuntimeException("Return value " + result);
      }
    } catch (IOException | InterruptedException | RuntimeException ex) {
      throw new RuntimeException(cmd[0] + " " + cmd[1] + " " + cmd[2] + ": " + ex);
    }

    // Now do something with the lines extracted by Tesseract
    Path hocrFile = OCRPaths.getHocrPath(id);
    logger.info("Starting parse of html file from OCR: " + hocrFile);
    ISourceDocumentContents docInstance = readOCRResults2(pageIndex, hocrFile);

    // The readOCRResults method is done with the hocrFile, so we can get rid of
    // it.
    try {
      Files.delete(hocrFile);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }

    return docInstance;
  }
}
