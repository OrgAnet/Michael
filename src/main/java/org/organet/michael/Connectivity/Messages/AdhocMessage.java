package org.organet.michael.Connectivity.Messages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.organet.michael.Helper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.organet.michael.App.APP_PACKAGE;

abstract public class AdhocMessage {
  public static String PART_SEPARATOR = "\t";

  private static final Logger logger = LogManager.getLogger(AdhocMessage.class.getName());

  private MessageDomains domain;
  private String command;
  private Object arguments;

  // TODO Add nonce and other NDN stuff here

  public AdhocMessage(MessageDomains domain, String command) {
    this.domain = domain;
    this.command = command.toLowerCase();
    arguments = null;
  }

  public AdhocMessage(MessageDomains domain, String command, Object arguments) {
    this.domain = domain;
    this.command = command.toLowerCase();
    this.arguments = arguments;
  }

  public MessageDomains getDomain() {
    return domain;
  }

  public String getCommand() {
    return command;
  }

  public Object getArguments() {
    return arguments;
  }

  public static <T extends AdhocMessage> T parse(String messageString) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    // Extract domain, command and argument (if any)
    String[] splittedMessageString = messageString.split(PART_SEPARATOR, 3); // TODO Test 'limit'

    String messageDomain = splittedMessageString[0];
    String messageCommand = splittedMessageString[1];
    Object messageArgument = null;

    if (splittedMessageString.length > 2 && !splittedMessageString[2].equals(PART_SEPARATOR)) {
      messageArgument = splittedMessageString[2];
    }

    Class<?> clazz = Class.forName(String.format(
      "%s.Connectivity.Messages.%s.%sMessage",
      APP_PACKAGE, messageDomain.toUpperCase(), Helper.toDisplayCase(messageCommand)));

    Constructor<?> ctor = (messageArgument == null)
      ? clazz.getConstructor()
      : clazz.getConstructor(Object.class);

    return (T) ctor.newInstance();
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
