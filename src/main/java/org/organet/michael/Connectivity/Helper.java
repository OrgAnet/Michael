package org.organet.michael.Connectivity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.organet.michael.CommandLineInterface.*;

import java.net.*;
import java.util.*;

public class Helper {
  public static final int ELECTION_SCORE_THRESHOLD = 5;

  private static final Logger logger = LogManager.getLogger(Helper.class.getName());
  private static final Level VERBOSE = Level.forName("VERBOSE", 550);

  private static final NetworkInterface adhocInterface;
  private static final byte[] rawMACAddress; // The MAC address of the ad-hoc network interface card
  private static final Inet4Address rawIPAddress; // The IP address of this very node on the ad-hoc network
  private static final InetAddress rawBroadcastAddress;
  private static final String macAddress;
  private static final String ipAddress;
  private static final String broadcastAddress;

  static {
    adhocInterface = obtainAdhocInterface();
    rawMACAddress = obtainMACAddress();
    rawIPAddress = obtainIPAddress();
    rawBroadcastAddress = obtainBroadcastAddress();

    macAddress = convertMACAddress(rawMACAddress);
    ipAddress = stringifyInetAddress(rawIPAddress);
    broadcastAddress = stringifyInetAddress(rawBroadcastAddress);
  }

  private static NetworkInterface obtainAdhocInterface() {
    String adhocInterfaceName = org.organet.michael.CommandLineInterface.Manager.getInstance().getAdhocInterfaceName();
    NetworkInterface adhocInterface;
    try {
      adhocInterface = NetworkInterface.getByName(adhocInterfaceName);
    } catch (SocketException e) {
      logger.fatal("Could not get ad-hoc network interface. Terminating...");

      System.exit(2);
      return null;
    }

    return adhocInterface;
  }

  private static NetworkInterface obtainAdhocInterface_old() {
    Enumeration<NetworkInterface> interfaces;
    List<NetworkInterface> adhocInterfaceCandidates = new ArrayList<>();

    try {
      interfaces = NetworkInterface.getNetworkInterfaces();
    } catch (SocketException e) {
      logger.fatal("Could not get network interfaces. Terminating...");

      System.exit(1);
      return null;
    }

    int interfacesCount = 0;

    while (interfaces.hasMoreElements()) {
      NetworkInterface theInterface = interfaces.nextElement();

      interfacesCount += 1;
      logger.log(VERBOSE, "Found network interface: \"{}\".", theInterface.getName());

      // The interface MUST NOT be loopback
      try {
        if (theInterface.isLoopback()) {
          continue; // Continue with the next interface since this one is loopback
        }
      } catch (SocketException e) {
        continue; // Continue with the next interface since this interface is erroneous
      }

      // The interface MUST be a physical one
      if (theInterface.isVirtual()) {
        continue;
      }

      adhocInterfaceCandidates.add(theInterface);
    }

    if (adhocInterfaceCandidates.isEmpty()) {
      logger.fatal("There is no ad-hoc network interface found. Terminating...");

      System.exit(2);
      return null;
    }

    logger.log(VERBOSE, "{} network interface(s) found on the system.", interfacesCount);

    return electAdhocInterface(adhocInterfaceCandidates);
  }

  private static NetworkInterface electAdhocInterface(List<NetworkInterface> candidateInterfaces) {
    logger.log(VERBOSE, "The most suitable network interface will be elected among {} candidates with the threshold being {}.",
      candidateInterfaces.size(), ELECTION_SCORE_THRESHOLD);

    SortedMap<Integer, NetworkInterface> interfacesAndScores = new TreeMap<>();

    for (NetworkInterface candidate : candidateInterfaces) {
      int score = 0;

      char[] candidateNameCharacters = candidate.getName().toCharArray();
      if (candidateNameCharacters[0] == 'w') {
        score += 1;
      }
      if (candidateNameCharacters[1] == 'l') {
        score += 1;
      }
      if (candidateNameCharacters[2] == 'a' || candidateNameCharacters[2] == 'p') {
        score += 1;
      }
      if (candidateNameCharacters[2] == 'n') {
        score += 1;
      }

      if (candidateNameCharacters[candidateNameCharacters.length - 1] >= '0' &&
        candidateNameCharacters[candidateNameCharacters.length - 1] <= '9') {
        score += 1;
      }

      if (candidate.getInterfaceAddresses().size() > 0) {
        score += 1;
      }

      // Check if the interface has IPv4 address bound
      Enumeration<InetAddress> addresses = candidate.getInetAddresses();

      while (addresses.hasMoreElements()) {
        InetAddress theAddress = addresses.nextElement();

        if (theAddress instanceof Inet4Address) {
          score += 1;

          // Check if the address starts with "192.168"
          String[] theAddressOctets = stringifyInetAddress(theAddress).split("\\.");

          if (theAddressOctets[0].equals("192")) {
            score += 1;
          }
          if (theAddressOctets[0].equals("168")) {
            score += 1;
          }
        }
      }

      logger.log(VERBOSE, "Network interface named \"{}\" has score of {}.", candidate.getName(), score);

      interfacesAndScores.put(score, candidate);
    }

    if (((Integer) ((TreeMap) interfacesAndScores).lastEntry().getKey()) <= ELECTION_SCORE_THRESHOLD) {
      logger.fatal("No suitable network interface has been found for ad-hoc networking. Terminating...");

      System.exit(5);
    }

    NetworkInterface electedInterface = (NetworkInterface) ((TreeMap) interfacesAndScores).lastEntry().getValue();

    logger.info("\"{}\" is elected as ad-hoc network interface.", electedInterface.getName());

    // Last entry has the highest score
    return (electedInterface);
  }

  private static byte[] obtainMACAddress() {
    try {
      return adhocInterface.getHardwareAddress();
    } catch (SocketException e) {
      logger.fatal("Could not get MAC address of the ad-hoc network interface. Terminating...");

      System.exit(3);
      return null;
    }
  }

  private static Inet4Address obtainIPAddress() {
    Enumeration<InetAddress> addresses = adhocInterface.getInetAddresses();

    while (addresses.hasMoreElements()) {
      InetAddress theAddress = addresses.nextElement();

      logger.log(VERBOSE, "IP found on ad-hoc network interface: {}", stringifyInetAddress(theAddress));

      if (theAddress instanceof Inet4Address) {
        // TODO Do more checks on the addresses if necessary

        return (Inet4Address) theAddress;
      }
    }

    return null;
  }

  private static InetAddress obtainBroadcastAddress() {
    List<InterfaceAddress> addresses = adhocInterface.getInterfaceAddresses();

    for (InterfaceAddress theAddress : addresses) {
      if (theAddress.getAddress().equals(rawIPAddress)) {
        return theAddress.getBroadcast();
      }
    }

    return null;
  }

  private static String convertMACAddress(byte[] macAddress) {
    StringBuilder sb = new StringBuilder();

    for (int i = 0, len = macAddress.length; i < len; i++) {
      sb.append(String.format("%02X%s", macAddress[i], (i < len - 1) ? "-" : ""));
    }

    return sb.toString();
  }

  private static String stringifyInetAddress(InetAddress ipAddress) {
    if (ipAddress == null) {
      return null;
    }

    if (ipAddress instanceof Inet6Address) {
      return ipAddress.getHostAddress();
    } else {
      return ipAddress.toString().replaceAll("^/+", "");
    }
  }

  public static String getAdhocInterfaceName() {
    return adhocInterface.getName();
  }

  public static String getMACAddress() {
    return macAddress;
  }

  public static String getIPAddress() {
    return ipAddress;
  }

  public static String getBroadcastAddress() {
    return broadcastAddress;
  }
}
