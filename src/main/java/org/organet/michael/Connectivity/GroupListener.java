package org.organet.michael.Connectivity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class GroupListener implements Runnable {
  private static final Logger logger = LogManager.getLogger(GroupListener.class.getName());
  private static final Level VERBOSE = Level.forName("VERBOSE", 550);

  private static final String GROUP_ADDRESS = "224.8.15.16";
  private static final int GROUP_PORT = 2342;

  private final Manager manager;

  private final MulticastSocket groupSocket;

  public GroupListener(Manager manager) throws IOException {
    this.manager = manager;

    groupSocket = new MulticastSocket(GROUP_PORT);

    InetAddress group = InetAddress.getByName(GROUP_ADDRESS);
    groupSocket.joinGroup(group);
  }

  @Override
  public void run() {
    logger.info("Group listening on {}:{}.", GROUP_ADDRESS, GROUP_PORT);

    byte[] buf = new byte[1000];
    DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);
    //noinspection InfiniteLoopStatement
    while (true) {
      try {
        groupSocket.receive(receivedPacket);

        logger.log(VERBOSE, "Packet received: {}", receivedPacket.getData());
      } catch (IOException e) {
        logger.warn("Could not able to receive from group listener.");

        // TODO Is this error recurring or one-time? If recurring terminate(or do smth)
      }
    }
  }
}
