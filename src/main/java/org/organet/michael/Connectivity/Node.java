package org.organet.michael.Connectivity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.organet.michael.Connectivity.Messages.AdhocMessage;
import org.organet.michael.Connectivity.Messages.MessageDomains;
import org.organet.michael.Connectivity.Messages.NODE.*;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class Node implements Runnable {
  private static final Logger logger = LogManager.getLogger(Node.class.getName());

  private Socket socket;
  private BufferedReader inlet;
  private OutputStream outlet;
  private String outputBuffer = "";
  private String deviceID = null;
  private volatile boolean shutdown = false;

  Node(Socket nodeSocket) throws IOException {
    socket = nodeSocket;
    socket.setSoTimeout(200); // TODO Doc here - timeout for introduce message reply
    inlet = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    outlet = socket.getOutputStream();

    // Tell the new node to introduce itself
    send(new IntroduceMessage());
  }

  @Override
  public void run() {
    String line;
    //noinspection InfiniteLoopStatement
    while (true) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();

        // Give this socket a last chance to send last message (if any)
        shutdown = true;
        continue;
      }

      // Check if anything to be sent
      if (outputBuffer.length() > 0) {
        try {
          outlet.write(String.valueOf(outputBuffer + "\n\r").getBytes());
          outlet.flush();
        } catch (IOException e) {
          logger.warn("Could not send the message to node.");
        }

        // Also 'flush' the 'buffer'
        outputBuffer = "";
      }

      if (shutdown) {
        // After send last message (if any) stop listening
        break;
      }

      try {
        if ((line = inlet.readLine()) == null) {
          continue;
        }
      } catch (SocketTimeoutException e) {
        logger.warn("Node connection will be closed due to reply for introduce message is timed out.");

        // Do NOT give a last chance to send last message (if any)
        break;
      } catch (IOException e) {
        // e.printStackTrace();

        // Give this socket a last chance to send last message (if any)
        shutdown = true;
        continue;
      }

      line = line.replaceAll("(\\r|\\n)", "");
      if (line.length() == 0) {
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

          // Since we have our reply for the introduce message we can
          // reset the socket read timeout.
          try {
            socket.setSoTimeout(0);
          } catch (SocketException e) {
            e.printStackTrace(); // FIXME
          }
        }
      } else {
        Manager.processMessage(deviceID, line);
      }
    }

    try {
      releaseResources();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  String getDeviceID() {
    return deviceID;
  }

  private void send(AdhocMessage message) {
    outputBuffer = message.toString();
  }

  void disconnect() {
    // Stop listening
    shutdown = true;

    // Notify the node
    send(new DisconnectMessage());
  }

  private void releaseResources() throws IOException {
    inlet.close(); // FIXME Expect an I/O exception since socket is already closed
    outlet.close();
    socket.close();
  }
}
