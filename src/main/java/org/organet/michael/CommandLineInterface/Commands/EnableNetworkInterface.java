package org.organet.michael.CommandLineInterface.Commands;

import java.util.Arrays;

public class EnableNetworkInterface extends CommandBase {
  public EnableNetworkInterface(String adhocInterfaceName) {
    super("/sbin/ip", Arrays.asList("link", "set", adhocInterfaceName, "up"));
  }
}
