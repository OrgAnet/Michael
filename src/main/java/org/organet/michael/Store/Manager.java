package org.organet.michael.Store;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.organet.michael.Connectivity.Messages.AdhocMessage;
import org.organet.michael.Connectivity.Node;
import org.organet.michael.ProcessesMessage;

public class Manager implements ProcessesMessage {
  private static final Logger logger = LogManager.getLogger(Manager.class.getName());

  public static void processMessage(Node node, AdhocMessage message) {
    logger.info("Message received from {}: {}", node.getDeviceID(), message.getCommand());
    //
  }
}
