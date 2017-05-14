package org.organet.michael.Connectivity.Messages.FILE_SYSTEM;

import org.organet.michael.Connectivity.Messages.AdhocMessage;
import org.organet.michael.Connectivity.Messages.MessageDomains;

public class ReceiveMessage extends AdhocMessage {
  // FIXME Accept SharedFileHeader as argument to pass to the underlying AdhocMessage argument
  public ReceiveMessage(Object fileSize) {
    super(MessageDomains.FILE_SYSTEM, "receive", fileSize);
  }
}
