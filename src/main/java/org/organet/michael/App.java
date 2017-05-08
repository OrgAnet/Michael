package org.organet.michael;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.organet.michael.Connectivity.Helper;
import org.organet.michael.Connectivity.Manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {
  private static final Logger logger = LogManager.getLogger(App.class.getName());

  public static final int APP_PORT = 5169;
  static final String deviceID = calculateDeviceID();

//  static ContentStore localIndex; // TODO Implement ContentStore after server

  public static void main(String[] args) {
    if (args.length < 1) {
      logger.fatal("Shared directory path is missing, first argument must be a valid path. Terminating...");

      return;
    }

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
//    for (int i = 0; i < idBaseLen; i++) {
//      int rnd;
//
//      do {
//        rnd = (int) Math.floor(Math.random() * idBaseLen);
//      } while (charactersOrder.contains(rnd));
//
//      charactersOrder.add(rnd);
//    }

    // Finally construct the device identifier by appending it with characters
    StringBuilder idBuilder = new StringBuilder(idBaseLen);
    for (int i = 0; i < idBaseLen; i++) {
      idBuilder.append(characters[charactersOrder.get(i)]);
    }

    return idBuilder.toString();
  }
}
