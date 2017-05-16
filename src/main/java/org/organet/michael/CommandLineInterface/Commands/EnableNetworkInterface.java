package org.organet.michael.CommandLineInterface.Commands;

import org.organet.michael.CommandLineInterface.ReturnsResponse;

import java.util.Arrays;

public class EnableNetworkInterface extends CommandBase implements ReturnsResponse {
  public EnableNetworkInterface(String adhocInterfaceName) {
    super("/sbin/ip", Arrays.asList("link", "set", adhocInterfaceName, "up"));
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
