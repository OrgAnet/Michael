package org.organet.michael.Connectivity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class Helper {
  private static final Logger logger = LogManager.getLogger(Helper.class.getName());

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
    Enumeration<NetworkInterface> interfaces;
    List<NetworkInterface> adhocInterfaceCandidates = new ArrayList<>();

    try {
      interfaces = NetworkInterface.getNetworkInterfaces();
    } catch (SocketException e) {
      logger.fatal("Could not get network interfaces. Terminating...");

      System.exit(1);
      return null;
    }

    while (interfaces.hasMoreElements()) {
      NetworkInterface theInterface = interfaces.nextElement();

      // // The interface MUST be up
      // try {
      //   if (!theInterface.isUp()) {
      //     continue; // Continue with the next interface since this one is not up
      //   }
      // } catch (SocketException e) {
      //   continue; // Continue with the next interface since this interface is erroneous
      // }
      //
      // NOTE Since there is an implication on Java's net library (`isUp` returns false even if \
      //      the interface is up) we simply skip this (is up) check

      // The interface MUST NOT be loopback
      try {
        if (theInterface.isLoopback()) {
          continue; // Continue with the next interface since this one is loopback
        }
      } catch (SocketException e) {
        continue; // Continue with the next interface since this interface is erroneous
      }

      // TODO Find out virtual interfaces are acceptable \
      // But for now we are not accept them
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

    return findAdhocInterface(adhocInterfaceCandidates);
  }

  private static NetworkInterface findAdhocInterface(List<NetworkInterface> candidateInterfaces) {
    SortedMap<Integer, NetworkInterface> interfacesAndScores = new TreeMap<>();

    for (NetworkInterface candidate : candidateInterfaces) {
      int score = 0;

      if (candidate.getParent() == null) {
        score += 1;
      }

      if (candidate.getDisplayName().startsWith("wlan")) {
        score += 3;
      } else if (candidate.getDisplayName().startsWith("wl")) {
        score += 2;
      } else if (candidate.getDisplayName().startsWith("w")) {
        score += 1;
      }

      if (candidate.getInterfaceAddresses().size() > 0) {
        score += 1;
      }

      interfacesAndScores.put(score, candidate);
    }

    // Last entry has the highest score
    return ((NetworkInterface) ((TreeMap) interfacesAndScores).lastEntry().getValue());
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

      if (theAddress instanceof Inet4Address) {
        // TODO Do more checks on the addresses if necessary

        return (Inet4Address) theAddress;
      }
    }

    return null;
  }

  private static InetAddress obtainBroadcastAddress() {
    List<InterfaceAddress> addresses = adhocInterface.getInterfaceAddresses();
    Iterator<InterfaceAddress> addressIterator = addresses.iterator();

    while (addressIterator.hasNext()) {
      InterfaceAddress theAddress = addressIterator.next();

      if (ipAddress == null) {
        // Return first address' broadcast address
        return theAddress.getBroadcast();
      } else if (theAddress.getAddress().equals(rawIPAddress)) {
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
    return ipAddress.toString().replaceAll("^/+", "");
  }

  public static String getMACAddress() {
    return macAddress;
  }

  static String getIPAddress() {
    return ipAddress;
  }

  public static String getBroadcastAddress() {
    return broadcastAddress;
  }
}
