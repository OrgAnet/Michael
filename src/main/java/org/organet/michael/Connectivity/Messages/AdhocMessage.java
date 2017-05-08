package org.organet.michael.Connectivity.Messages;

abstract public class AdhocMessage {
  public static String PART_SEPARATOR = ":";
//  public static String PART_SEPARATOR = "\1 ";

  private MessageDomains domain;
  private String command;
  private Object arguments;

  // TODO Add nonce and other NDN stuff here

  public AdhocMessage(MessageDomains domain, String command) {
    this.domain = domain;
    this.command = command.toLowerCase();
    arguments = null;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    sb.append(domain)
      .append(PART_SEPARATOR)
      .append(command);

    if (arguments != null) {
      sb.append(PART_SEPARATOR)
        .append(arguments.toString());
    }

    return sb.toString();
  }
}
