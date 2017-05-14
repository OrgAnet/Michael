package org.organet.michael.Connectivity.Messages.FILE_SYSTEM;

import org.organet.michael.Connectivity.Messages.AdhocMessage;
import org.organet.michael.Connectivity.Messages.MessageDomains;

public class GetMessage extends AdhocMessage {
  public GetMessage(Object filename) {
    super(MessageDomains.FILE_SYSTEM, "get", filename);
  }
}
