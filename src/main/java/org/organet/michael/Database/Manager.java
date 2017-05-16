package org.organet.michael.Database;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Manager {
  private static final Logger logger = LogManager.getLogger(Manager.class.getName());
  private static final Level VERBOSE = Level.forName("VERBOSE", 550);

  // Singleton reference
  private static Manager thisInst = new Manager();

  private Manager() {
    // This ctor is private and empty. The reason of these is to
    // hide/avoid the instantiation of this class except itself.
  }

  public void initialize() {
    // TODO Check if DB file exist - if no create DB and table(s)
    // TODO Connect to the DB
  }

  public static Manager getInstance() {
    return thisInst;
  }
}
