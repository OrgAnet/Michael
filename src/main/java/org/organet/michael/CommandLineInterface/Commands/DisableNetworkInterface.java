package org.organet.michael.CommandLineInterface.Commands;

import org.organet.michael.CommandLineInterface.ReturnsResponse;

import java.util.Arrays;

public class DisableNetworkInterface extends CommandBase implements ReturnsResponse {
  public DisableNetworkInterface(String adhocInterfaceName) {
    super("/sbin/ip", Arrays.asList("link", "set", adhocInterfaceName, "down"));
  }

  @Override
  public Object getResponse() {
    // TODO the command
    Object response = this.run();

    // TODO Parse the output
    // TODO Return meaningful response
    return true;
  }
}
