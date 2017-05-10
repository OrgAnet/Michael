package org.organet.michael.Connectivity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static org.organet.michael.App.APP_PORT;

public class Listener implements Runnable {
  private static final Logger logger = LogManager.getLogger(Listener.class.getName());

  private final ServerSocket serverSocket;

  Listener() {
    ServerSocket tempSocket;
    try {
      tempSocket = new ServerSocket(APP_PORT, 50, InetAddress.getByName(Helper.getIPAddress()));
    } catch (IOException e) {
      logger.fatal("Could not create listener serverSocket, port might be occupied. Terminating...");

      System.exit(4);
      tempSocket = null;
    }
    serverSocket = tempSocket;
  }

  @Override
  public void run() {
    logger.info("Listening on {}:{}.", Helper.getIPAddress(), APP_PORT);

    //noinspection InfiniteLoopStatement
    while (true) {
      Socket clientSocket;
      try {
        clientSocket = serverSocket.accept();
      } catch (IOException e) {
        logger.error("Could not establish client connection.");

        continue;
      }

      // Create new Node instance and pass the `clientSocket`
      if (!Manager.createAndAddNewNode(clientSocket)) {
        logger.warn("Established client connection can not be handled further.");

        try {
          clientSocket.close();
        } catch (IOException e) {
          logger.warn("Established client connection can not be closed.");
        }
      }
    }
  }
}
