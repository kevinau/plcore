package org.plcore.userio.path;

import java.util.List;

import org.plcore.userio.path.parser.NodePathParser;
import org.plcore.userio.path.parser.TokenMgrError;

public class PathParser {

  public static IPathExpression parse (String s) throws ParseException {
    IPathExpression expr;
    try {
      expr = new NodePathParser().parse(s);
    } catch (org.plcore.userio.path.parser.ParseException | TokenMgrError ex) {
      throw new ParseException(ex);
    }
    return expr;
  }
  
  
  public static IPathExpression[] parse (String[] ss) throws ParseException {
    IPathExpression[] exprs = new IPathExpression[ss.length];
    NodePathParser parser = new NodePathParser();
    try {
      int i = 0;
      for (String s : ss) {
        exprs[i] = parser.parse(s);
        i++;
      }
    } catch (org.plcore.userio.path.parser.ParseException | TokenMgrError ex) {
      throw new ParseException(ex);
    }
    return exprs;
  }
  

  public static IPathExpression[] parse (List<String> ss) throws ParseException {
    IPathExpression[] exprs = new IPathExpression[ss.size()];
    NodePathParser parser = new NodePathParser();
    try {
      int i = 0;
      for (String s : ss) {
        exprs[i] = parser.parse(s);
        i++;
      }
    } catch (org.plcore.userio.path.parser.ParseException | TokenMgrError ex) {
      throw new ParseException(ex);
    }
    return exprs;
  }
  

}
