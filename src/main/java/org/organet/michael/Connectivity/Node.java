package org.organet.michael.Connectivity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.organet.michael.Connectivity.Messages.AdhocMessage;
import org.organet.michael.Connectivity.Messages.MessageDomains;
import org.organet.michael.Connectivity.Messages.NODE.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import static org.organet.michael.Connectivity.Messages.MessageDomains.NODE;

public class Node implements Runnable {
  private static final Logger logger = LogManager.getLogger(Node.class.getName());

  private final Manager manager = Manager.getInstance();

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
        if (!line.startsWith(NODE.toString() + AdhocMessage.PART_SEPARATOR + "introduce")) {
          // The first message that the node sent MUST be introduction message
          // (as a response to introduction request message). Otherwise the
          // socket MUST be closed.
          //
          // Assign random device id (0-100) to remove this very node from the manager's list
          deviceID = String.valueOf((int) Math.floor(Math.random() * 101));
          manager.disconnectFrom(deviceID);
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
        processMessage(line);
      }
    }

    try {
      releaseResources();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getDeviceID() {
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
    inlet.close();
    outlet.close();
    socket.close();
  }

  private void processMessage(String messageString) {
    // Check if the message relevant to the Node (the domain is NODE - this class).
    // Else pass the message to the Manager for further processing

    AdhocMessage message;
    try {
      message = AdhocMessage.parse(messageString);
    } catch (ClassNotFoundException e) {
      logger.error("Message class ({}) could not be found. The message will be ignored.");

      return;
    } catch (NoSuchMethodException e) {
      e.printStackTrace();

      return;
    } catch (InstantiationException e) {
      e.printStackTrace();

      return;
    } catch (IllegalAccessException e) {
      e.printStackTrace();

      return;
    } catch (InvocationTargetException e) {
      e.printStackTrace();

      return;
    }

    if (message.getDomain() == NODE) {
      //
    } else {
      Manager.processMessage(this, message);
    }
  }
}
