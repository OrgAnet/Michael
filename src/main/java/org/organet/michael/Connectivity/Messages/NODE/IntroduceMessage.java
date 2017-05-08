package org.organet.michael.Connectivity.Messages.NODE;

import org.organet.michael.Connectivity.Messages.AdhocMessage;
import org.organet.michael.Connectivity.Messages.MessageDomains;

public class IntroduceMessage extends AdhocMessage {
  public IntroduceMessage() {
    super(MessageDomains.NODE, "introduce");
  }
}
