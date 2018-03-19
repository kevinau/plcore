/*******************************************************************************
 * Copyright (C) 2018 Kevin Holloway (kholloway@pennyledger.org)
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.plcore.time;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.plcore.text.DayDateFormat;


public abstract class DateFactory {

  public static final DayDateFormat entryDateFormat = new DayDateFormat("dd/MM/yy");
  
  //private static final DateTimeFormatter isoFormat = DateTimeFormat.forPattern("yyyy-MM-dd");
  
  public static final int OK = 0;
  public static final int REQUIRED = 1;
  public static final int INCOMPLETE = 2;
  public static final int ERROR = 3;
  
  private static String[] months = {
    "january", "february", "march", "april", "may", "june",
    "july", "august", "september", "october", "november", "december",
  };
  private static int[] normalMthDays = {
    31, 28, 31, 30, 31, 30, 
    31, 31, 30, 31, 30, 31,
  };
  private static int[] leapMthDays = {
    31, 29, 31, 30, 31, 30, 
    31, 31, 30, 31, 30, 31,
  };
  private static String[] ordinals = {
    "st", "nd", "rd", "th",
  };
  

  private static String entryDateFormat (int year, int month, int day) {
    StringBuilder buffer = new StringBuilder(8);
    if (day < 10) {
      buffer.append('0');
    }
    buffer.append(day);
    buffer.append('/');
    if (month < 10) {
      buffer.append('0');
    }
    buffer.append(month);
    buffer.append('/');
    year = year % 100;
    if (year < 10) {
      buffer.append('0');
    }
    buffer.append(year);
    return buffer.toString();
  }

  
  private static String mmyyDateFormat (int year, int month) {
    StringBuilder buffer = new StringBuilder(5);
    if (month < 10) {
      buffer.append('0');
    }
    buffer.append(month);
    buffer.append('/');
    year = year % 100;
    if (year < 10) {
      buffer.append('0');
    }
    buffer.append(year);
    return buffer.toString();
  }

  
  private static String mmDateFormat (int month) {
    StringBuilder buffer = new StringBuilder(2);
    if (month < 10) {
      buffer.append('0');
    }
    buffer.append(month);
    return buffer.toString();
  }

  
  private static String yyDateFormat (int year) {
    StringBuilder buffer = new StringBuilder(2);
    year = year % 100;
    if (year < 10) {
      buffer.append('0');
    }
    buffer.append(year);
    return buffer.toString();
  }

  
  /**
   * Parse a string that contains a date.  The string can contain a day, month and year, as follows:
   * <ul>
   * <li>If the fields are delimited by spaces or punctuation, or if the month is an alpha field:
   * <ul>
   * <li>The day can be one or two digits.</li>
   * <li>The month can be one or two digits, or alpha characters that describe a month.</li>
   * <li>The year can be two or four digits.</li>
   * </ul>
   * </li>
   * <li>If the fields are all numbers and are packed tight (no spaces):
   * <ul>
   * <li>The day must be two digits.</li>
   * <li>The month must be two digits.</li>
   * <li>The year can be two or four digits.</li>
   * </ul>
   * </li>
   * </ul>
   * <p>
   * Note.  There is a Javascript version of this method in jquery.ui.datepicker.  Any changes to this
   * method should be changed in jquery.ui.datepicker.
   * 
   * @param source
   * @param fillYMD
   * @param msg
   * @param resultYMD
   * @param completion
   * @return an integer reporting the validity of the source string
   */
  public static int validate (String source, int[] fillYMD, String[] msg, int[] resultYMD, String[] completion) {
    if (fillYMD == null) {
      Calendar today = Calendar.getInstance();
      fillYMD = new int[] {
          today.get(Calendar.YEAR),
          today.get(Calendar.MONTH) + 1,
          today.get(Calendar.DAY_OF_MONTH),
      };
    }
    int year = fillYMD[0];
    int month = fillYMD[1];
    int day = fillYMD[2];
    
    char[] c = source.toCharArray();
    if (c.length == 0) {
      completion[0] = entryDateFormat(year, month, day);
      msg[0] = null;
      return REQUIRED;
    }
    completion[0] = null;
    
    State state = new State();
    parseString(source, state);
    if (state.type == ERROR) {
      msg[0] = "invalid date";
      return ERROR;
    }
    if (state.type == INCOMPLETE) {
      msg[0] = "invalid date";
      // TODO completion not set
      return INCOMPLETE;
    }
    
    resultYMD[0] = state.year;
    resultYMD[1] = state.month + 1;
    resultYMD[2] = state.day;
    return OK; 
  }
  
  
  private static final Pattern isoPattern = Pattern.compile("([12]\\d\\d\\d)-(\\d\\d)-(\\d\\d)");

  private static final int NOTPARSED = Integer.MIN_VALUE;
  

  
  private static boolean parseISODate (String s, State state) {
    Matcher matcher = isoPattern.matcher(s);
    if (matcher.lookingAt()) {
      // Get year, month and day values
      state.year = Integer.parseInt(matcher.group(1));
      state.month = Integer.parseInt(matcher.group(2)) - 1;
      state.day = Integer.parseInt(matcher.group(3));
      state.cursor = 10;
      state.validateDayMonthYear();
      return true;
    } else {
      if (matcher.hitEnd()) {
        state.incompleteISO = true;
      } else {
        state.incompleteISO = false;
      }
      return false;
    }
  }
  
  private static boolean parseNNOrdinalDay (char[] c, State state) {
    int i = state.cursor;
    if (i == c.length) {
      state.type = INCOMPLETE;
      return false;
      
    }
    int i0 = i;
    if (Character.isDigit(c[i])) {
      int n = 0;
      while (i < c.length && i < i0 + 2 && Character.isDigit(c[i])) {
        n = n * 10 + Character.digit(c[i], 10);
        i++;
      }
      if (i == i0 + 2) {
        if (n < 1 || n > 31) {
          state.type = ERROR;
          state.message = "bad day (" + n + ")";
          return true;
        }
      }
      if (i < c.length && Character.isLetter(c[i])) {
        int cursor[] = new int[1];
        int index[] = new int[1];
        int match = matchAlpha(c, i, ordinals, cursor, index);
        switch (match) {
        case OK :
          state.cursor = cursor[0];
          state.isOrdinal = true;
          state.day = n;
          //state.validateDayMonthYear();
         break;
        case INCOMPLETE :
          state.type = INCOMPLETE;
          break;
        case ERROR :
          // Its not an ordinal, but it is an NN
          state.cursor = i;
          state.isOrdinal = false;
          state.day = n;
          //state.validateDayMonthYear();
          break;
        }
      } else {
        state.cursor = i;
        state.isOrdinal = false;
        state.day = n;
        //state.validateDayMonthYear();
      }
      return true;
    } else {
      return false;
    }
  }
  
  
  private static void parsePunctuation1 (char[] c, State state) {
    int i = state.cursor;
    if (i == c.length) {
      state.type = INCOMPLETE;
      return;
    }
    
//    state.validateDayMonthYear();
//    if (state.type == ERROR) {
//      return;
//    }
    
    switch (c[i]) {
    case '-' :
    case '/' :
    case '.' :
    case ',' :
    case ' ' :
      state.firstPunctuation = c[i];
      i++;
      while (i < c.length && c[i] == ' ') {
        i++;
      }
      state.cursor = i;
      break;
    default :
      if (Character.isLetterOrDigit(c[i])) {
        state.firstPunctuation = 0;
      } else {
        state.type = ERROR;
        state.message = "unknown character after '" + new String(c, 0, i) + "'";
      }
      break;
    }
  }
  
  
  private static void parsePunctuation2 (char[] c, State state) {
    int i = state.cursor;
    switch (state.firstPunctuation) {
    case 0 :
      if (state.isAlphaMonth) {
        parsePunctuation1(c, state);
      } else {
        state.validateDayMonthYear();
      }
      break;
    case ' ' :
      if (i == c.length) {
        state.type = INCOMPLETE;
        return;
      }
      
      state.validateDayMonthYear();
      if (state.type == ERROR) {
        return;
      }
      
      if (c[i] == ',' || c[i] == ' ') {
        i++;
        while (i < c.length && c[i] == ' ') {
          i++;
        }
        state.cursor = i;
      } else if (Character.isDigit(c[i])) {
        // No delimiter
      } else {
        state.type = ERROR;
        state.message = "expecting ',' or spaces after '" + new String(c, 0, i) + "'";
      }
      break;
    default :
      if (i == c.length) {
        state.type = INCOMPLETE;
        return;
      }

      state.validateDayMonthYear();
      if (state.type == ERROR) {
        return;
      }
      
      if (c[i] == state.firstPunctuation) {
        i++;
        state.cursor = i;
      } else {
        state.type = ERROR;
        state.message = "expecting '" + state.firstPunctuation + "' after '" + new String(c, 0, i) + "'";
      }
      break;
    }
  }
  
  
  private static void parseNNAlphaMonth (char[] c, State state) {
    int i = state.cursor;
    if (i == c.length) {
      state.type = INCOMPLETE;
      return;
    }

    int i0 = i;
    int n = 0;
    if (Character.isDigit(c[i])) {
      while (i < c.length && i < i0 + 2 && Character.isDigit(c[i])) {
        n = n * 10 + Character.digit(c[i], 10);
        i++;
      }
      if (i == i0 + 2) {
        if (n <= 0 || n > 12) {
          state.type = ERROR;
          state.message = "bad month (" + n + ")";
        }
      }
      if (state.firstPunctuation == 0 && i == c.length && i < i0 + 2) {
        state.type = INCOMPLETE;
        return;
      }
      state.cursor = i;
      state.isAlphaMonth = false;
      state.month = n - 1;
      //state.validateDayMonthYear();
    } else {
      parseAlphaMonth(c, state);
    }
  }
      
  
  private static boolean parseAlphaMonth (char[] c, State state) {
    int i = state.cursor;
    int i0 = i;
    if (i < c.length && Character.isLetter(c[i])) {
      int cursor[] = new int[1];
      int index[] = new int[1];
      int match = matchAlpha(c, i, months, cursor, index);
      switch (match) {
      case OK :
        state.cursor = cursor[0];
        state.isAlphaMonth = true;
        state.month = index[0];
        //state.validateDayMonthYear();
        break;
      case INCOMPLETE :
        state.type = INCOMPLETE;
        break;
      case ERROR :
        state.type = ERROR;
        state.message = "expecting named month but found '" + new String(c, i0, i - i0) + "'";
        break;
      }
      return true;
    } else {
      return false;
    }
  }
      
  
  private static void parseYear (char[] c, State state) {
    int i = state.cursor;
    int i0 = i;
    
    if (i == c.length) {
      state.type = INCOMPLETE;
      return;
    }
    int n = 0;
    if (Character.isDigit(c[i])) {
      while (i < c.length && i < i0 + 4) {
        if (Character.isDigit(c[i])) {
          n = n * 10 + Character.digit(c[i], 10);
          i++;
        } else {
          state.type = ERROR;
          return;
        }
      }
      switch (i - i0) {
      case 1 :
      case 3 :
        state.type = INCOMPLETE;
        break;
      case 2 :
        if (n < 50) {
          n += 2000;
        } else {
          n += 1900;
        }
        state.cursor = i;
        state.year = n;
        state.validateDayMonthYear();
        break;
      case 4 :
        state.cursor = i;
        state.year = n;
        state.validateDayMonthYear();
        break;
      }
    } else {
      state.type = ERROR;
      state.message = "expecting 2 or 4 digit year after '" + new String(c, 0, i0) + "'";
    }
  }
      
  
  private static int matchAlpha(char[] c, int i, String[] alphas, int[] cursor, int[] index) {
    int i0 = i;
    while (i < c.length && Character.isLetter(c[i])) {
      i++;
    }
    int alpha = 0;
    int alphaCount = 0;
    String aaa = new String(c, i0, i - i0).toLowerCase();
    for (int a = 0; a < alphas.length; a++) {
      if (alphas[a].startsWith(aaa)) {
        alpha = a;
        alphaCount++;
      }
    }
    switch (alphaCount) {
    case 0 :
      // Alphabetic characters not recognized
      return ERROR;
    case 1 :
      // Single string of alphabetic characters recognized
      cursor[0] = i;
      index[0] = alpha;
      return OK;
    default :
      // Multiple strings of alphabetic characters recognised
      if (i == c.length) {
        return INCOMPLETE;
      } else {
        return ERROR;
      }
    }
  }
  

  private static void parseEnd(char[] c, State state) {
    int i = state.cursor;
    if (i < c.length) {
      state.type = ERROR;
      state.message = "excess characters '" + new String(c, i, c.length - i) + "' after date";
    }
  }
  

  private static class State {
    int type = OK;
    int cursor;
    boolean incompleteISO;
    boolean isOrdinal;
    boolean isAlphaMonth;
    int day = NOTPARSED;
    int month = NOTPARSED;
    int year = NOTPARSED;
    char firstPunctuation;
    String message;
    
    private void validateDayMonthYear () {
      System.out.println("validate " + day + " / " + month + " / " + year);
      //if (day != NOTPARSED && month != NOTPARSED) {
        int[] mthDays;
        if (year == NOTPARSED) {
          mthDays = leapMthDays;
        } else if ((year % 4) == 0) {
          mthDays = leapMthDays;
        } else {
          mthDays = normalMthDays;
        }
        if (day > mthDays[month]) {
          type = ERROR;
          message = "bad day (" + day + ") for month " + (month + 1); 
          return;
        }
      //}
    }
    
  }
  
  
  private static void parseString (String source, State state) {
    char[] c = source.toCharArray();

    boolean matched = parseISODate(source, state);
    if (matched) {
      if (state.type != OK) {
        return;
      }
      parseEnd(c, state);
      return;
    }
    
    System.out.println(">>>>>>>>>>>>> " + source);
    parseString2 (c, state);
    if (state.type == ERROR && state.incompleteISO) {
      state.type = INCOMPLETE;
    }
  }
  
  
  private static void parseString2 (char[] c, State state) {
    // The source has not matched a ISO 8601 date, so try ddmmyy or similar.
    // At the start of the date, we are looking for digits (day or month), or ordinal
    boolean matched = parseNNOrdinalDay(c, state);
    if (matched) {
      if (state.type != OK) {
        return;
      }
      if (state.isOrdinal) {
        parsePunctuation1(c, state);
        if (state.type != OK) {
          return;
        }
        parseNNAlphaMonth(c, state);
        if (state.type != OK) {
          return;
        }
        parsePunctuation2(c, state);
        if (state.type != OK) {
          return;
        }
        parseYear(c, state);
        if (state.type != OK) {
          return;
        }
        parseEnd(c, state);
        return;
      } else {
        parsePunctuation1(c, state);
        if (state.type != OK) {
          return;
        }
        parseNNAlphaMonth(c, state);
        if (state.type != OK) {
          return;
        }
        parsePunctuation2(c, state);
        if (state.type != OK) {
          return;
        }
        parseYear(c, state);
        if (state.type != OK) {
          return;
        }
        parseEnd(c, state);
        return;
      }
    } else {
      matched = parseAlphaMonth(c, state);
      if (matched) {
        if (state.type != OK) {
          return;
        }
        parsePunctuation1(c, state);
        if (state.type != OK) {
          return;
        }
        parseNNOrdinalDay(c, state);
        if (state.type != OK) {
          return;
        }
        parsePunctuation2(c, state);
        if (state.type != OK) {
          return;
        }
        parseYear(c, state);
        if (state.type != OK) {
          return;
        }
        parseEnd(c, state);
        return;
      }
    }
    state.type = ERROR;
    return;
  }
    
}
