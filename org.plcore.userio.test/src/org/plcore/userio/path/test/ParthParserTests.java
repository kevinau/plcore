package org.plcore.userio.path.test;

import java.util.Arrays;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.plcore.test.Assert;
import org.plcore.test.ITestCase;
import org.plcore.test.Test;
import org.plcore.userio.path.IPathExpression;
import org.plcore.userio.path.ParseException;
import org.plcore.userio.path.PathParser;

@Component
public class ParthParserTests implements ITestCase {

  @Test
  public void simpleValidPath() {
    IPathExpression expr = PathParser.parse("inner/0/-1/field/*/**");
    Assert.assertNotNull(expr);
  }
  
  @Test(expected = ParseException.class)
  public void simpleInvalidPath() {
    PathParser.parse("////inner/field");
  }
  
  @Test(expected = ParseException.class)
  public void lexerInvalidPath() {
    PathParser.parse("@inner/field");
  }

  @Test
  public void arrayOfPaths() {
    String[] paths = {
        "inner",
        "inner/field",
    };
    IPathExpression[] expr = PathParser.parse(paths);
    Assert.assertNotNull(expr);
  }
  
  @Test
  public void listOfPaths() {
    String[] paths = {
        "inner",
        "inner/field",
    };
    List<String> list = Arrays.asList(paths);
    IPathExpression[] expr = PathParser.parse(list);
    Assert.assertNotNull(expr);
  }
  

  @Test
  public void dumpComplexPath() {
    // Used for coverage only
    IPathExpression expr = PathParser.parse("inner/0/-1/field/*/**");
    Assert.assertNotNull(expr);
  }
}
