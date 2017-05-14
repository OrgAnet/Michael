package org.organet.michael.Store;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.organet.michael.Connectivity.Messages.AdhocMessage;
import org.organet.michael.Connectivity.Node;
import org.organet.michael.ProcessesMessage;

public class Manager extends ManagerBase {
  private static final Logger logger = LogManager.getLogger(Manager.class.getName());
  // Singleton reference
  private static final Manager thisInst = new Manager();

  private Manager() {
    // This ctor is private and empty. The reason of these is to
    // hide/avoid the instantiation of this class except itself.
  }

  public static Manager getInstance() {
    return thisInst;
  }
  }
}
