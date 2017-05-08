package org.organet.michael.Connectivity;

import org.organet.michael.App;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener implements Runnable {
  private final ServerSocket serverSocket;

  Listener() {
    ServerSocket tempSocket;
    try {
      tempSocket = new ServerSocket(App.APP_PORT, 50, InetAddress.getByName(Helper.getIPAddress()));
//      tempSocket = new ServerSocket(App.APP_PORT);
    } catch (IOException e) {
      System.out.println("Error: Could not create listener serverSocket, port might be occupied. Terminating...");

      System.exit(4);
      tempSocket = null;
    }
    serverSocket = tempSocket;
  }

  @Override
  public void run() {
    System.out.format("Listening on %s:%d", Helper.getIPAddress(), App.APP_PORT);

    //noinspection InfiniteLoopStatement
    while (true) {
      Socket clientSocket;
      try {
        clientSocket = serverSocket.accept();
      } catch (IOException e) {
        System.out.println("Error: Could not establish client connection.");

        continue;
      }

      // Create new Node instance and pass the `clientSocket`
      if (!Manager.createAndAddNewNode(clientSocket)) {
        System.out.println("Warning: Established client connection can not be handled further.");

        try {
          clientSocket.close();
        } catch (IOException e) {
          System.out.println("Warning: Established client connection can not be closed.");
        }
      }
    }
  }
}
