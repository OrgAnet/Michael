package org.organet.michael.CommandLineInterface.Commands;

import org.organet.michael.CommandLineInterface.ReturnsResponse;

import java.util.Arrays;

public class CheckNetworkManagerStatus extends CommandBase implements ReturnsResponse {
  public CheckNetworkManagerStatus() {
    super("service", Arrays.asList("NetworkManager", "status"));

    setReportExitStatus(false);
  }

  public Object getResponse() {
    // Run the command
    String result = this.run();
    if (result == null) {
      return null;
    }

    // Parse the output
    result = result.split("Active: ")[1];

    // Return meaningful result
    return result.startsWith("active");
  }
}
