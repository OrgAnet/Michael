package org.organet.michael.CommandLineInterface.Commands;

import org.organet.michael.CommandLineInterface.ReturnsResponse;
import org.organet.michael.CommandLineInterface.SetsESSID;

import java.util.Arrays;

public class IwconfigSetESSID extends CommandBase implements SetsESSID, ReturnsResponse {
  public IwconfigSetESSID(String adhocInterfaceName, String essid) {
    // TODO `channel` MUST be between 1 and 14 (both inclusive)
    super("/sbin/iwconfig", Arrays.asList(
      adhocInterfaceName, "essid", essid));
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
