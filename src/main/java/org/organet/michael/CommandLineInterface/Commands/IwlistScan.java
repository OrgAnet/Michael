package org.organet.michael.CommandLineInterface.Commands;

import org.organet.michael.CommandLineInterface.AdhocNetwork;
import org.organet.michael.CommandLineInterface.ScansForAdhocNetworks;
import org.organet.michael.Connectivity.Helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IwlistScan extends CommandBase implements ScansForAdhocNetworks {
  private IwlistScan() {
    super("/usr/sbin/iwlist", new ArrayList<>(Arrays.asList(Helper.getAdhocInterfaceName(), "scan")));
  }

  @Override
  public List<AdhocNetwork> scanAdhocNetworks() {
    // TODO Run the command
    // TODO Parse the output
    // TODO Return meaningful result
    return null;
  }
}
