package org.organet.michael.Connectivity.Messages.STORE;

import org.organet.michael.Connectivity.Messages.AdhocMessage;
import org.organet.michael.Connectivity.Messages.MessageDomains;

public class GetMessage extends AdhocMessage {
  public GetMessage() {
    super(MessageDomains.STORE, "get");
  }
}
