package org.organet.michael.Connectivity.Messages;

// NOTE The entries on this enum MUST have corresponding
//      packages under App.APP_PACKAGE package and also
//      these packages MUST have manager class (named
//      Manager) which implements `HandlesMessage`
//      interface.
public enum MessageDomains {
  NODE, // General, Node, commands
  STORE,
}
