package org.plcore.userio.path.test;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.plcore.userio.path.IPathExpression;
import org.plcore.userio.path.ParseException;
import org.plcore.userio.path.PathParser;

public class ParthParserTests {

  @Test
  public void simpleValidPath() {
    IPathExpression expr = PathParser.parse("inner/0/-1/field/*/**");
    Assertions.assertNotNull(expr);
  }
  
  @Test
  public void simpleInvalidPath() {
    Assertions.assertThrows(ParseException.class, () -> PathParser.parse("////inner/field"));
  }
  
  @Test
  public void lexerInvalidPath() {
    Assertions.assertThrows(ParseException.class, () -> PathParser.parse("@inner/field"));
  }

  @Test
  public void arrayOfPaths() {
    String[] paths = {
        "inner",
        "inner/field",
    };
    IPathExpression[] expr = PathParser.parse(paths);
    Assertions.assertNotNull(expr);
  }
  
  @Test
  public void listOfPaths() {
    String[] paths = {
        "inner",
        "inner/field",
    };
    List<String> list = Arrays.asList(paths);
    IPathExpression[] expr = PathParser.parse(list);
    Assertions.assertNotNull(expr);
  }
  

  @Test
  public void dumpComplexPath() {
    // Used for coverage only
    IPathExpression expr = PathParser.parse("inner/0/-1/field/*/**");
    Assertions.assertNotNull(expr);
  }
}
