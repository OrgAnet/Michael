package org.organet.michael.CommandLineInterface.Commands;

import org.organet.michael.CommandLineInterface.ActivatesAdhocMode;
import org.organet.michael.CommandLineInterface.ReturnsResponse;

import java.util.Arrays;

public class IwActivateAdhocMode extends CommandBase implements ActivatesAdhocMode, ReturnsResponse {
  public IwActivateAdhocMode(String adhocInterfaceName) {
    super("/sbin/iw", Arrays.asList(adhocInterfaceName, "set", "type", "ibss"));
  }

  @Override
  public Object getResponse() {
    // Run the command
    Object response = this.run();

    // Parse the output
    String responseString = String.valueOf(response);

    // Return meaningful response
    return (!responseString.startsWith("command failed:") && !responseString.startsWith("Usage:"));
  }
}
