package org.organet.michael.CommandLineInterface.Commands;

import org.organet.michael.CommandLineInterface.ReturnsResponse;

import java.util.Arrays;

public class IwActivateAdhocMode extends CommandBase implements ReturnsResponse {
  public IwActivateAdhocMode(String adhocInterfaceName) {
    super("/sbin/iw", Arrays.asList(adhocInterfaceName, "set", "type", "ibss"));
  }

  @Override
  public Object getResponse() {
    // Run the command
    Object response = this.run();

    // Parse the output
    // Return meaningful response
    return (!String.valueOf(response).startsWith("command failed"));
  }
}
