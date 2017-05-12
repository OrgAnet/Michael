package org.organet.michael.FileSystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.organet.michael.Connectivity.Messages.ReflectionName;
import org.organet.michael.ManagerBase;

public class Manager extends ManagerBase {
  private static final Logger logger = LogManager.getLogger(Manager.class.getName());
  // Singleton reference
  private static Manager thisInst = new Manager();

  private Manager() {
    // This ctor is private and empty. The reason of these is to
    // hide/avoid the instantiation of this class except itself.
  }

  public static Manager getInstance() {
    return thisInst;
  }

  @ReflectionName("get")
  private void sendFile(Object sharedFileID) {
    logger.info("Sending the file with the ID of {}...", sharedFileID);

    // TODO
  }
}
