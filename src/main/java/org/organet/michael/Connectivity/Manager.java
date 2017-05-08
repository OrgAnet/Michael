package org.organet.michael.Connectivity;

import org.organet.michael.Connectivity.Messages.AdhocMessage;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Manager {
  private static Listener listener = new Listener();
  private static List<Node> nodes = new ArrayList<>();

  static boolean createAndAddNewNode(Socket nodeSocket) {
    Node newNode;
    try {
      newNode = new Node(nodeSocket);
    } catch (IOException e) {
      System.out.println("Warning: Could not create new Node.");

      return false;
    }

    // NOTE Threads can be controlled if stored
    (new Thread(newNode)).start();

    return nodes.add(newNode);
  }

  public static void listen() {
    // NOTE Threads can be controlled if stored
    (new Thread(listener)).start();
  }

  static void discover() {
    // TODO Find other nodes in the ad-hoc network
  }

  static void broadcast(AdhocMessage message) {
    // TODO Iterate through the nodes and send the message to all of them
  }

  static void processMessage(String nodeDeviceID, String messageString) {
    // TODO Convert `messageString` to AdhocMessage
  }

  private static int getNodeIndexByDeviceID(String nodeDeviceID) {
    for (int i = 0, len = nodes.size(); i < len; i++) {
      if (nodes.get(i).getDeviceID().equals(nodeDeviceID)) {
        return i;
      }
    }

    return -1;
  }

  static Node getNodeByDeviceID(String nodeDeviceID) {
    for (Node node : nodes) {
      if (node.getDeviceID().equals(nodeDeviceID)) {
        return node;
      }
    }

    return null;
  }

  static boolean disconnectFrom(String nodeDeviceID) {
    int targetNodeIndex = getNodeIndexByDeviceID(nodeDeviceID);

    if (targetNodeIndex == -1) {
      return true;
    }

    try {
      nodes.get(targetNodeIndex).disconnect();
    } catch (IOException e) {
      System.out.println("Error: Could not disconnect from node.");

      return false;
    }

    return (nodes.remove(targetNodeIndex) != null);
  }
}
