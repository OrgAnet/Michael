package org.organet.michael.CommandLineInterface.Commands;

import java.util.Arrays;

public class DisableNetworkInterface extends CommandBase {
  public DisableNetworkInterface(String adhocInterfaceName) {
    super("/sbin/ip", Arrays.asList("link", "set", adhocInterfaceName, "down"));
  }
}
