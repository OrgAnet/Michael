package org.organet.michael;

import org.organet.michael.Connectivity.Messages.AdhocMessage;
import org.organet.michael.Connectivity.Node;

public interface ProcessesMessage {
  static void processMessage(Node node, AdhocMessage message) {
    // NOTE This method will be overwritten by implementor
    System.out.format("%s says %s", node.getDeviceID(), message.toString());
  }
}
