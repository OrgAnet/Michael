package org.organet.michael.Connectivity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.organet.michael.App;
import org.organet.michael.Connectivity.Messages.AdhocMessage;
import org.organet.michael.Connectivity.Messages.FILE_SYSTEM.GetMessage;
import org.organet.michael.Connectivity.Messages.NODE.*;
import org.organet.michael.FileSystem.SharedFile;
import org.organet.michael.Store.Repository;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.organet.michael.Connectivity.Messages.MessageDomains.NODE;

public class Node implements Runnable {
  private static final Logger logger = LogManager.getLogger(Node.class.getName());
  private static final Level VERBOSE = Level.forName("VERBOSE", 550);

  private static final int RECEIVE_BUFFER_SIZE = 1024; // bytes

  private final Manager manager = Manager.getInstance();

  private Socket socket;
  private final Repository<AdhocMessage> messages;
  private BufferedReader inlet;
  private OutputStream outlet;
  private List<AdhocMessage> outletBuffer = new ArrayList<>(); // FIFO
  private String deviceID = null;
  private volatile boolean shutdown = false;

  private FileInputStream fis = null;
  private FileOutputStream fos = null;
  private long fileSize = 0;
  private String filename = "received_1k.bin"; // FIXME Do NOT hard-code this
  private String filePath; // NOTE This will be calculated on-the-fly

  Node(Socket nodeSocket, Repository<AdhocMessage> messages) throws IOException {
    socket = nodeSocket;
    this.messages = messages;
    socket.setSoTimeout(190); // TODO Doc here - timeout for introduce message reply
    inlet = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    outlet = socket.getOutputStream();

    filePath = Paths.get(App.getSharedDirectoryPath(), filename).toAbsolutePath().toString();
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
      if (outletBuffer.size() > 0) {
        // Get next message and remove from the 'buffer'
        AdhocMessage messageToSend = outletBuffer.remove(0);

        try {
          outlet.write(String.valueOf(messageToSend + "\n\r").getBytes());
          outlet.flush();

          // Critical section for `messages`
          synchronized (messages) {
            messages.add(messageToSend); // TODO Add destination device id and ip maybe?
          }
        } catch (IOException e) {
          logger.warn("Could not send the message to node.");
        }
      } else if (fis != null) {
        // Send a file
        logger.log(VERBOSE, "Sending the file...");

        byte[] fileBytes = new byte[Math.toIntExact(fileSize)]; // FIXME Probably erroneous
        BufferedInputStream bis = new BufferedInputStream(fis);
        try {
          bis.read(fileBytes, 0, fileBytes.length);
        } catch (IOException e) {
          e.printStackTrace(); // FIXME

          return;
        }

        try {
          outlet.write(fileBytes, 0, fileBytes.length);
          outlet.flush();
          fis.close();
          fis = null; // This is important

          logger.info("File sent.");
        } catch (IOException e) {
          e.printStackTrace(); // FIXME

          return;
        }
      } else if (fos != null) {
        // Receiving a file
        logger.log(VERBOSE, "Receiving the file...");

        InputStream sis;
        try {
          sis = socket.getInputStream();
        } catch (IOException e) {
          e.printStackTrace(); // FIXME

          return;
        }
        byte[] fileBytes = new byte[RECEIVE_BUFFER_SIZE];
        try {
          fos = new FileOutputStream(filePath); // TODO Ask for the download location as local path
          // TODO What if file already exists?
        } catch (FileNotFoundException e) {
          e.printStackTrace(); // FIXME

          break;
        }
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead;
        try {
          bytesRead = sis.read(fileBytes, 0, RECEIVE_BUFFER_SIZE);
        } catch (IOException e) {
          e.printStackTrace(); // FIXME R34D

          break;
        }
        int current = bytesRead;

        while (current < fileSize) {
          try {
            bytesRead = sis.read(fileBytes, current, (RECEIVE_BUFFER_SIZE - current));
          } catch (IOException e) {
            e.printStackTrace(); // FIXME R34D

            break;
          }

          if (bytesRead >= 0) {
            current += bytesRead;
          } else {
            break; // receive finished
          }
        }
        System.out.println("Info: File received.");

        try {
          bos.write(fileBytes, 0 , current);
          bos.flush();
        } catch (IOException e) {
          e.printStackTrace(); // FIXME

          break;
        }

        // TODO finally {...
        try {
          if (fos != null) {
            fos.close();
            fos = null;
          }
          if (bos != null) {
            bos.close();
          }
        } catch (IOException e) {
          e.printStackTrace(); // FIXME

          break;
        }

        System.out.println("Info: File successfully received.");
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

      if (line.length() == 0) {
        continue;
      }

      // TODO Move the deviceID check to `handleMessage`
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

          send(new IntroduceMessage());
        }
      } else {
        handleMessage(line);
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

  public void send(AdhocMessage message) {
    outletBuffer.add(message);
  }

  public void send(SharedFile sharedFile) {
    try {
      fis = new FileInputStream(sharedFile.getAbsolutePath());
      fileSize = sharedFile.getSize();
    } catch (FileNotFoundException e) {
      e.printStackTrace(); // FIXME

      return;
    }

    logger.info("Ready to send the file.");
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

  private void handleMessage(String messageString) {
    // Check if the message relevant to the Node (the domain is NODE - this class).
    // Else pass the message to the Manager for further processing

    AdhocMessage message;
    try {
      message = AdhocMessage.parse(messageString);
    } catch (ClassNotFoundException e) {
      logger.error("Message class ({}) could not be found. The message will be ignored.", e.getMessage());

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
      manager.handleMessage(this, message);
    }
  }
}
