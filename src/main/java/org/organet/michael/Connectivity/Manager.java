package org.organet.michael.Connectivity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.organet.michael.Connectivity.Messages.AdhocMessage;
import org.organet.michael.Store.LRURepository;
import org.organet.michael.Store.Repository;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.organet.michael.App.APP_PACKAGE;

public class Manager {
  private static final Logger logger = LogManager.getLogger(Manager.class.getName());
  // Singleton reference
  private static final Manager thisInst = new Manager();

  private Listener listener;
  private GroupListener groupListener;
  private List<Node> nodes = new ArrayList<>();
  private Repository<AdhocMessage> messages = new LRURepository<>();

  private Manager() {
    // This ctor is private and empty. The reason of these is to
    // hide/avoid the instantiation of this class except itself.

    // FIXME Call this after IP address is obtained
    //listener = new Listener(this);

    try {
      groupListener = new GroupListener(this);
    } catch (IOException e) {
      logger.fatal("Could not create group listener socket. Terminating...");

      System.exit(40);
    }
  }

  public static Manager getInstance() {
    return thisInst;
  }

  void handleMessage(Node node, AdhocMessage message) {
    // Check if Manager class exists for the message
    String clazzName = org.organet.michael.Helper.toDisplayCase(message.getDomain().name()).replaceAll("\\_", "");
    Class<?> clazz;
    try {
      clazz = Class.forName(String.format("%s.%s.Manager", APP_PACKAGE, clazzName));
    } catch (ClassNotFoundException e) {
      logger.error("Manager class for the message ({}) could not be found. Message will be ignored.", clazzName);

      return;
    }

    // Check if `getInstance` method exists
    Method managerGetInstance;
    Object managerInst;
    try {
      managerGetInstance = clazz.getMethod("getInstance");
      managerInst = managerGetInstance.invoke(null);
    } catch (NoSuchMethodException e) {
      logger.error("Manager class can not handle the message. Message will be ignored.", clazzName);

      return;
    } catch (IllegalAccessException e) {
      e.printStackTrace(); // FIXME

      return;
    } catch (InvocationTargetException e) {
      e.printStackTrace(); // FIXME

      return;
    }

    // Check if `handleMessage` method exists
    Method managerMessageHandler;
    try {
      managerMessageHandler = clazz.getMethod("handleMessage", Node.class, AdhocMessage.class);
    } catch (NoSuchMethodException e) {
      logger.error("Manager class can not handle the message. Message will be ignored.", clazzName);

      return;
    }

    try {
      managerMessageHandler.invoke(managerInst, node, message); // TODO Send the relevant Manager instance as the first parameter
//      managerMessageHandler.invoke(null, node, message);
    } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
      logger.error("Manager class' message handler is not suitable. Message will be ignored.", clazzName);
    }
  }

  boolean createAndAddNewNode(Socket nodeSocket) {
    Node newNode;
    try {
      newNode = new Node(nodeSocket, messages);
    } catch (IOException e) {
      logger.warn("Could not create new Node.");

      return false;
    }

    logger.info("Client connection established on port {}.", nodeSocket.getPort());

    // NOTE Threads can be controlled if stored
    (new Thread(newNode)).start();

    return nodes.add(newNode);
  }

  public void listen() {
    // NOTE Threads can be controlled if stored
    (new Thread(listener)).start();
  }

  void discover() {
    // TODO Find other nodes in the ad-hoc network
  }

  void broadcast(AdhocMessage message) {
    // TODO Iterate through the nodes and send the message to all of them
  }

  private int getNodeIndexByDeviceID(String nodeDeviceID) {
    for (int i = 0, len = nodes.size(); i < len; i++) {
      if (nodes.get(i).getDeviceID().equals(nodeDeviceID)) {
        return i;
      }
    }

    return -1;
  }

  Node getNodeByDeviceID(String nodeDeviceID) {
    for (Node node : nodes) {
      if (node.getDeviceID().equals(nodeDeviceID)) {
        return node;
      }
    }

    return null;
  }

  boolean disconnectFrom(String nodeDeviceID) {
    int targetNodeIndex = getNodeIndexByDeviceID(nodeDeviceID);

    if (targetNodeIndex == -1) {
      return true;
    }

    nodes.get(targetNodeIndex).disconnect();

    return (nodes.remove(targetNodeIndex) != null);
  }
}
