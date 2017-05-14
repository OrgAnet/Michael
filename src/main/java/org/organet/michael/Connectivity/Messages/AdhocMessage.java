package org.organet.michael.Connectivity.Messages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.organet.michael.App;
import org.organet.michael.Helper;
import org.organet.michael.Store.HasID;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.organet.michael.App.APP_PACKAGE;

abstract public class AdhocMessage implements HasID {
  public static String PART_SEPARATOR = "\t";

  private static final Logger logger = LogManager.getLogger(AdhocMessage.class.getName());

  private MessageDomains domain;
  private String command;
  private Object arguments;

  private String messageID;
  private int nonce = 0;
  private int ttl = App.DEFAULT_MESSAGE_TTL;
  // TODO NDN stuff here

  public AdhocMessage(MessageDomains domain, String command) {
    this.domain = domain;
    this.command = command.toLowerCase();
    arguments = null;

    initialize();
  }

  public AdhocMessage(MessageDomains domain, String command, Object arguments) {
    this.domain = domain;
    this.command = command.toLowerCase();
    this.arguments = arguments;

    initialize();
  }

  private void initialize() {
    messageID = calculateMessageID();
    //
  }

  private String calculateMessageID() {
    // TODO Calculate unique message id using domain, command, device identifier and random
    StringBuilder sb = new StringBuilder();

    // MD5(domain.name+command)
    String hashedDomainAndCommand;

    byte[] dataBytes;
    try {
      dataBytes = String.format("%s%s", domain, command).getBytes("US-ASCII");
    } catch (UnsupportedEncodingException e) {
      logger.warn("ASCII encoding could not be found on the system's implementation." +
        "Implementation default charset will be used for calculating message identifier.");

      dataBytes = String.format("%s%s", domain, command).getBytes();
    }

    // Every implementation of the Java platform is required to support the following standard MessageDigest algorithm
    // @see http://docs.oracle.com/javase/8/docs/api/java/security/MessageDigest.html
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] mdbytes = md.digest(dataBytes);

      //convert the byte to hex format method 1
      StringBuilder mdsb = new StringBuilder();
      for (byte mdbyte : mdbytes) {
        mdsb.append(Integer.toString((mdbyte & 0xff) + 0x100, 16).substring(1));
      }

      hashedDomainAndCommand = mdsb.toString();
    } catch (NoSuchAlgorithmException e) {
      logger.warn("MD5 algorithm could not be found on the system's implementation." +
        "Plain text will be used for calculating message identifier.");

      hashedDomainAndCommand = String.format("%s%s", domain, command);
    }
    sb.append(hashedDomainAndCommand.substring(0, 10));

    // Append device identifier
    sb.append(App.getDeviceID());

    // Append pseudo-random
    DateFormat df = new SimpleDateFormat("ddHHmmss");
    Date currentDate = new Date();
    sb.append(df.format(currentDate));

    return sb.toString();
  }

  public String getID() {
    return messageID;
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
    String[] splittedMessageString = messageString.split(PART_SEPARATOR, 3);

    String messageDomain = splittedMessageString[0];
    String messageCommand = splittedMessageString[1];
    Object messageArgument = null;

    if (splittedMessageString.length > 2 && !splittedMessageString[2].equals(PART_SEPARATOR)) {
      messageArgument = splittedMessageString[2];
    }

    Class<?> clazz = Class.forName(String.format(
      "%s.Connectivity.Messages.%s.%sMessage",
      APP_PACKAGE, messageDomain.toUpperCase(), Helper.toDisplayCase(messageCommand)));

    if (messageArgument == null) {
      Constructor<?> ctor = clazz.getConstructor();
      return (T) ctor.newInstance();
    } else {
      Constructor<?> ctor = clazz.getConstructor(Object.class);
      return (T) ctor.newInstance(messageArgument);
    }
  }

  public int incrementNonce() {
    return ++nonce;
  }

  public int getTTL() {
    return ttl;
  }

  public int decrementTTL() {
    if (ttl == 0) {
      return 0;
    } else {
      return --ttl;
    }
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
