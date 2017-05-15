package org.organet.michael.CommandLineInterface.Commands;

import java.util.Arrays;

public class StartNetworkManager extends CommandBase {
  public StartNetworkManager() {
    super("/usr/sbin/service", Arrays.asList("NetworkManager", "start"));
  }
}
