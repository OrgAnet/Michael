package org.organet.michael.CommandLineInterface.Commands;

import org.organet.michael.CommandLineInterface.ReturnsResponse;
import org.organet.michael.CommandLineInterface.ScansForAdhocNetworks;
import org.organet.michael.Connectivity.Helper;

import java.util.ArrayList;
import java.util.Arrays;

public class IwlistScan extends CommandBase implements ScansForAdhocNetworks, ReturnsResponse {
  private IwlistScan() {
    super("/usr/sbin/iwlist", new ArrayList<>(Arrays.asList(Helper.getAdhocInterfaceName(), "scan")));
  }

  // TODO Search for "integration" in the project "wireless" and move the lines from there to here
  @Override
  public Object getResponse() {
    // TODO Run the command
    // TODO Parse the output
    // TODO Return meaningful response
    return null;
  }
}
