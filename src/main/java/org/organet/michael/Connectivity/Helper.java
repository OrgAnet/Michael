package org.organet.michael.Connectivity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.organet.michael.CommandLineInterface.Manager;

public class Helper {
  private static final Logger logger = LogManager.getLogger(Helper.class.getName());
  private static final Level VERBOSE = Level.forName("VERBOSE", 550);

  private static final AdhocNetwork adhocInterface = null;
  private static String ipAddress; // TODO

  static {
//    adhocInterface = Manager.getInstance().getAdhocInterface();
  }

  public static String getMACAddress() {
    return adhocInterface.getMacAddress();
  }

  public static String getIPAddress() {
    return ipAddress;
  }
}
