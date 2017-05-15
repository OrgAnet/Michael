package org.organet.michael.CommandLineInterface.Commands;

import java.util.Arrays;

public class StopNetworkManager extends CommandBase {
  public StopNetworkManager() {
    super("/usr/sbin/service", Arrays.asList("NetworkManager", "stop"));
  }
}
