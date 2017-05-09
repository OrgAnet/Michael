package org.organet.michael.Connectivity.Messages.NODE;

import org.organet.michael.Connectivity.Messages.AdhocMessage;
import org.organet.michael.Connectivity.Messages.MessageDomains;

public class HelloMessage extends AdhocMessage {
  public HelloMessage() {
    super(MessageDomains.NODE, "hello");
  }
}
