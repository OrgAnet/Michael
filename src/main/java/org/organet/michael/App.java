package org.organet.michael;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.organet.michael.Connectivity.Helper;
import org.organet.michael.Connectivity.Manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {
  private static final Logger logger = LogManager.getLogger(App.class.getName());
  private static String deviceID;
  private static File sharedDirectory;

  public static final int APP_PORT = 5169;

//  static ContentStore localIndex; // TODO Implement ContentStore after server

  public static void main(String[] args) {
    if (args.length < 1) {
      logger.warn("Shared directory path was not set, checking in the current directory with name \"organet_shared\"");

      sharedDirectory = new File("./organet_shared");
      if (!sharedDirectory.exists() || !sharedDirectory.isDirectory()) {
        logger.fatal("Shared directory path was missing and designated directory has not been found. Terminating...");

        return;
      }
    } else {
      sharedDirectory = new File(args[0]);
    }

    logger.info("Shared directory path is \"{}\".", sharedDirectory.getAbsolutePath());

    deviceID = calculateDeviceID();
    logger.info("MAC address is \"{}\", IP address is \"{}\" and broadcast address is \"{}\".",
      Helper.getMACAddress(), Helper.getIPAddress(), Helper.getBroadcastAddress());

//    localStore = new ContentStore(true);

    // Start listener
    Manager.listen();
  }

  private static String calculateDeviceID() {
    // Start with MAC address of the ad-hoc network interface
    String idBase = Helper.getMACAddress();

    // Remove dashes
    idBase = idBase.replace("-", "");

    // Arbitrarily shuffle the ID
    int idBaseLen = idBase.length();
    char[] characters = idBase.toCharArray();

    // Generate and fill the `charactersOrder` with random numbers
    // NOTE Hard-coded order guarantees that every time same device identifier \
    //      is going to be calculated for the same MAC address.
    List<Integer> charactersOrder = new ArrayList<>(Arrays.asList(10, 1, 5, 8, 0, 2, 6, 4, 7, 11, 3, 9));

    // Finally construct the device identifier by appending it with characters
    StringBuilder idBuilder = new StringBuilder(idBaseLen);
    for (int i = 0; i < idBaseLen; i++) {
      // Try to convert odd numbers to characters in ASCII table if adding 5 (53 in ASCII)
      // will not make them greater than 'f' so this way the device identifier might look
      // more like a random hexadecimal number rather than MAC address-based number.
      if (i % 2 != 0 || ((int) (characters[charactersOrder.get(i)]) - 48) > 5) {
        idBuilder.append(characters[charactersOrder.get(i)]);
      } else {
        idBuilder.append((char) (((int) (characters[charactersOrder.get(i)]) - 48) + 'a'));
      }
    }

    String deviceId = idBuilder.toString();

    logger.info("Device ID: {}", deviceId);

    return deviceId;
  }
}
