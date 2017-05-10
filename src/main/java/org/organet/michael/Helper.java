package org.organet.michael;

public class Helper {
  // @see http://stackoverflow.com/a/15738441/250453
  public static String toDisplayCase(String s) {
    // these cause the character following to be capitalized
    final String ACTIONABLE_DELIMITERS = " '-_/";

    StringBuilder sb = new StringBuilder();
    boolean capNext = true;

    for (char c : s.toCharArray()) {
      c = (capNext)
        ? Character.toUpperCase(c)
        : Character.toLowerCase(c);
      sb.append(c);
      capNext = (ACTIONABLE_DELIMITERS.indexOf((int) c) >= 0); // explicit cast not needed
    }

    return sb.toString();
  }
}
