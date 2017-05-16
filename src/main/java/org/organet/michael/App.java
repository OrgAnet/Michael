package org.organet.michael;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.organet.michael.CommandLineInterface.Commands.*;
import org.organet.michael.Connectivity.Helper;
import org.organet.michael.Connectivity.Manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {
  public static final int APP_PORT = 5169;
  public static final String APP_PACKAGE = "org.organet.michael";
  // Defines how much the message allowed to hop
  public static final int DEFAULT_MESSAGE_TTL = 3;

  // Indicates ad-hoc network management will be handled by the application or not
  // TODO Automate it via checking current network ESSID and mode
  private static final boolean CONNECT_AUTOMATICALLY = false;

  private static final Logger logger = LogManager.getLogger(App.class.getName());
  private static final Level VERBOSE = Level.forName("VERBOSE", 550);

  private static Manager connectivityManager;
  // NOTE Since FileSystem requires that `sharedDirectory`, assign it after obtain `sharedDirectory`
  private static org.organet.michael.FileSystem.Manager fileSystemManager;
  private static org.organet.michael.CommandLineInterface.Manager cliManager =
    org.organet.michael.CommandLineInterface.Manager.getInstance();

  private static String deviceID;
  private static File sharedDirectory;

//  static ContentStore localIndex; // TODO Implement ContentStore after server

  public static void main(String[] args) {
    if (args.length < 1) {
      logger.warn("Shared directory path was not set, checking in the current directory with name \"organet_shared\"");

      sharedDirectory = new File("organet_shared");
      if (!sharedDirectory.exists() || !sharedDirectory.isDirectory()) {
        logger.fatal("Shared directory path was missing and designated directory has not been found. Terminating...");

        return;
      }
    } else {
      sharedDirectory = new File(args[0]);
    }

    logger.info("Shared directory path is \"{}\".", sharedDirectory.getAbsolutePath());


    if (CONNECT_AUTOMATICALLY) {
      // Stop the NetworkManager service to avoid interference if it is started,
      // and also add the hook to start it again when the application exits.
      Object response = (new CheckNetworkManagerStatus()).getResponse();
      if (response == null || (boolean) response) {
        cliManager.runWithPrivileges(new StopNetworkManager());
        // TODO Do NOT bring up the interface back for now. If it is \
        //      explicitly defined by the CLI arguments then bring up.
        //      Runtime.getRuntime().addShutdownHook(new Thread(
        //        () -> cliManager.runWithPrivileges(new StartNetworkManager())
        //      ));
      }

      //    String adhocInterfaceName = cliManager.getAdhocInterfaceName();
      if (!cliManager.activateAdhocMode()) {
        logger.fatal("Could not activate ad-hoc mode on network interface. Terminating...");

        System.exit(150);
        return;
      }
      //    (new IwActivateAdhocMode(adhocInterfaceName)).run();
      if (!cliManager.setESSID("deneme", 7)) {
        logger.fatal("Could not set ESSID on network interface. Terminating...");

        System.exit(150);
        return;
      }
      //    (new IwSetESSID(adhocInterfaceName, "deneme", 1)).run();
      //    cliManager.setIPAddress("192.168.42.1");
      //    (new IpSetAddress(adhocInterfaceName, "192.168.42.1")).run(); // TODO Check if it is REALLY necessary
      if (!cliManager.enableNetworkInterface()) {
        logger.fatal("Could not enable network interface. Terminating...");

        System.exit(150);
        return;
      }
      //    (new EnableNetworkInterface(adhocInterfaceName)).run();
    }

    connectivityManager = Manager.getInstance();


    deviceID = "tmp_dev_id";
//    deviceID = calculateDeviceID();
//    logger.log(VERBOSE, "Ad-hoc interface's; Name is \"{}\", MAC address is \"{}\" and IP address is \"{}\".",
//      Helper.getAdhocInterfaceName(), Helper.getMACAddress(), Helper.getIPAddress());


    // NOTE Disable FS Watcher for debugging purposes only
//    fileSystemManager = org.organet.michael.FileSystem.Manager.getInstance();
//
////    localStore = new ContentStore(true);
//
//    // Start watcher
//    fileSystemManager.start(sharedDirectory.getAbsolutePath());
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
    //      is going to be calculated for the same MAC address. The order SHOULD \
    //      be kept secret as much as possible to avoid MAC address exposure if \
    //      that is the case.
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

  public static String getDeviceID() {
    return deviceID;
  }

  public static String getSharedDirectoryPath() {
    return sharedDirectory.getPath();
  }
}
