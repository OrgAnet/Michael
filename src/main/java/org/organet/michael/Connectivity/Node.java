package org.organet.michael.Connectivity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.organet.michael.Connectivity.Messages.AdhocMessage;
import org.organet.michael.Connectivity.Messages.MessageDomains;
import org.organet.michael.Connectivity.Messages.NODE.*;

import java.io.*;
import java.net.Socket;

public class Node implements Runnable {
  private static final Logger logger = LogManager.getLogger(Node.class.getName());

  private Socket socket;
  private BufferedReader inlet;
  private OutputStream outlet;
  private String deviceID = null;
  private volatile boolean shutdown = false;

  Node(Socket nodeSocket) throws IOException {
    socket = nodeSocket;
    inlet = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    outlet = socket.getOutputStream();

    // Tell the new node to introduce itself
    sendMessage(new IntroduceMessage());
  }

  @Override
  public void run() {
    String line;
    //noinspection InfiniteLoopStatement
    while (!shutdown) {
      try {
        line = inlet.readLine();
        // TODO Use `inlet.ready()` if has an value
        if (line == null) {
          continue;
        }
      } catch (IOException e) {
        // e.printStackTrace();

        shutdown = true;
        continue;
      }

      if (deviceID == null) {
        if (!line.startsWith(MessageDomains.NODE.toString() + AdhocMessage.PART_SEPARATOR + "introduce")) {
          // The first message that the node sent MUST be introduction message
          // (as a response to introduction request message). Otherwise the
          // socket MUST be closed.
          //
          // Assign random device id (0-100) to remove this very node from the manager's list
          deviceID = String.valueOf((int) Math.floor(Math.random() * 101));
          Manager.disconnectFrom(deviceID);
          logger.warn("Node was disconnected due to lack of information.");

          shutdown = true;
        } else {
          deviceID = line.split(AdhocMessage.PART_SEPARATOR)[2];
          logger.info("Node device identifier acquired: " + deviceID);

          sendMessage(new HelloMessage());
        }
      } else {
        Manager.processMessage(deviceID, line);
      }
    }
  }

  String getDeviceID() {
    return deviceID;
  }

  private void sendMessage(AdhocMessage message) {
    try {
      outlet.write(String.valueOf(message + "\n\r").getBytes());
      outlet.flush();
    } catch (IOException e) {
      logger.warn("Could not send the message to node.");
    }
  }

  void disconnect() throws IOException {
    // Stop listening
    shutdown = true;

    // Notify the node
    sendMessage(new DisconnectMessage());

    outlet.flush();
    inlet.close(); // FIXME Expect an I/O exception since socket is already closed
//    outlet.close();
    socket.close();
  }
}
