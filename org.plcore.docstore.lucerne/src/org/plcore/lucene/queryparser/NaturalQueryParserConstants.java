/* Generated By:JavaCC: Do not edit this line. NaturalQueryParserConstants.java */
package org.plcore.lucene.queryparser;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface NaturalQueryParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int _NUM_CHAR = 1;
  /** RegularExpression Id. */
  int _ESCAPED_CHAR = 2;
  /** RegularExpression Id. */
  int _TERM_START_CHAR = 3;
  /** RegularExpression Id. */
  int _TERM_CHAR = 4;
  /** RegularExpression Id. */
  int _WHITESPACE = 5;
  /** RegularExpression Id. */
  int _QUOTED_CHAR = 6;
  /** RegularExpression Id. */
  int AND = 8;
  /** RegularExpression Id. */
  int OR = 9;
  /** RegularExpression Id. */
  int NOT = 10;
  /** RegularExpression Id. */
  int PLUS = 11;
  /** RegularExpression Id. */
  int MINUS = 12;
  /** RegularExpression Id. */
  int LPAREN = 13;
  /** RegularExpression Id. */
  int RPAREN = 14;
  /** RegularExpression Id. */
  int OP_COLON = 15;
  /** RegularExpression Id. */
  int OP_EQUAL = 16;
  /** RegularExpression Id. */
  int OP_LESSTHAN = 17;
  /** RegularExpression Id. */
  int OP_LESSTHANEQ = 18;
  /** RegularExpression Id. */
  int OP_MORETHAN = 19;
  /** RegularExpression Id. */
  int OP_MORETHANEQ = 20;
  /** RegularExpression Id. */
  int CARAT = 21;
  /** RegularExpression Id. */
  int QUOTED = 22;
  /** RegularExpression Id. */
  int RANGE_TO = 23;
  /** RegularExpression Id. */
  int TERM = 24;
  /** RegularExpression Id. */
  int FUZZY_SLOP = 25;
  /** RegularExpression Id. */
  int REGEXPTERM = 26;
  /** RegularExpression Id. */
  int RANGEIN_START = 27;
  /** RegularExpression Id. */
  int RANGEEX_START = 28;
  /** RegularExpression Id. */
  int RANGEIN_END = 29;
  /** RegularExpression Id. */
  int RANGEEX_END = 30;
  /** RegularExpression Id. */
  int RANGE_QUOTED = 31;
  /** RegularExpression Id. */
  int RANGE_GOOP = 32;
  /** RegularExpression Id. */
  int NUMBER = 33;

  /** Lexical state. */
  int Boost = 0;
  /** Lexical state. */
  int Range = 1;
  /** Lexical state. */
  int DEFAULT = 2;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "<_NUM_CHAR>",
    "<_ESCAPED_CHAR>",
    "<_TERM_START_CHAR>",
    "<_TERM_CHAR>",
    "<_WHITESPACE>",
    "<_QUOTED_CHAR>",
    "<token of kind 7>",
    "<AND>",
    "<OR>",
    "<NOT>",
    "\"+\"",
    "\"-\"",
    "\"(\"",
    "\")\"",
    "\":\"",
    "\"=\"",
    "\"<\"",
    "\"<=\"",
    "\">\"",
    "\">=\"",
    "\"^\"",
    "<QUOTED>",
    "\"...\"",
    "<TERM>",
    "<FUZZY_SLOP>",
    "<REGEXPTERM>",
    "\"[\"",
    "\"{\"",
    "\"]\"",
    "\"}\"",
    "<RANGE_QUOTED>",
    "<RANGE_GOOP>",
    "<NUMBER>",
  };

}
