package org.organet.michael.CommandLineInterface.Commands;

import org.organet.michael.CommandLineInterface.ReturnsResponse;
import org.organet.michael.CommandLineInterface.ScansForAdhocNetworks;

import java.util.*;

public class IwlistScan extends CommandBase implements ScansForAdhocNetworks, ReturnsResponse {
  public IwlistScan(String adhocInterfaceName) {
    super("/sbin/iwlist", Arrays.asList(adhocInterfaceName, "scan"));
  }

  // TODO Search for "integration" in the project "wireless" and move the lines from there to here
  @Override
  public Object getResponse() {
    // Run the command
    Object response = this.run();
    if (response == null) {
      return false;
    }

    String responseString = String.valueOf(response);

    // Parse the output
    List<Map<String, String>> networks = new ArrayList<>();
    String[] splitted = responseString.split("Cell \\d\\d - ");
    for (int i = 1, len = splitted.length; i < len; i++) {
      // Address, ESSID, Protocol, Mode, Frequency
      Map<String, String> network = new HashMap<>(5);
      String[] lines = splitted[i].split("\n");

      for (int j = 0, linesCount = lines.length; j < linesCount; j++) {
        String line = lines[j].trim();
        if (!line.contains(":")) {
          continue;
        }

        String[] keyValue = line.split(":", 2);
        if ("address, essid, protocol, mode, frequency".contains(keyValue[0].toLowerCase())) {
          network.put(keyValue[0].toLowerCase(), keyValue[1]);
        }
      }

      if (network.get("mode").toLowerCase().equals("ad-hoc")) {
        networks.add(network);
      }
    }

    // Return meaningful response
    return networks;
  }
}
