package org.organet.michael.Connectivity.Messages.NODE;

import org.organet.michael.Connectivity.Messages.AdhocMessage;
import org.organet.michael.Connectivity.Messages.MessageDomains;

public class DisconnectMessage extends AdhocMessage {
  public DisconnectMessage() {
    super(MessageDomains.NODE, "disconnect");
  }
}
