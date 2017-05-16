package org.organet.michael.CommandLineInterface.Commands;

import org.organet.michael.CommandLineInterface.ActivatesAdhocMode;
import org.organet.michael.CommandLineInterface.ReturnsResponse;

import java.util.Arrays;

public class IwconfigActivateAdhocMode extends CommandBase implements ActivatesAdhocMode, ReturnsResponse {
  public IwconfigActivateAdhocMode(String adhocInterfaceName) {
    super("/sbin/iwconfig", Arrays.asList(adhocInterfaceName, "mode", "ad-hoc"));
  }

  @Override
  public Object getResponse() {
    // Run the command
    Object response = this.run();

    // TODO Parse the output
    // TODO Return meaningful response
    return true;
  }
}
