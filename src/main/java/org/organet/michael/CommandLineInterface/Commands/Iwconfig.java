package org.organet.michael.CommandLineInterface.Commands;

import org.organet.michael.CommandLineInterface.ReturnsResponse;

import java.util.ArrayList;
import java.util.List;

public class Iwconfig extends CommandBase implements ReturnsResponse {
  public Iwconfig() {
    super("/sbin/iwconfig");

    setReportExitStatus(true);
  }

  @Override
  public Object getResponse() {
    // Run the command
    String result = this.run();
    if (result == null) {
      return null;
    }

    // Parse the output
    List<String> interfaceNamesThatSupportWirelessExtensions = new ArrayList<>();
    String[] lines = result.split("\n");
    for (String line : lines) {
      line = line.trim();

      if (line.equals("")) {
        continue;
      }

      if (line.contains("IEEE 802.11g")) {
        StringBuilder sb = new StringBuilder();
        char c;

        for (int i = 0, len = line.length(); i < len; i++) {
          c = line.charAt(i);

          if (c == ' ') {
            break;
          }

          sb.append(c);
        }

        interfaceNamesThatSupportWirelessExtensions.add(sb.toString());
      }
    }

    if (interfaceNamesThatSupportWirelessExtensions.size() == 0) {
      return null;
    }

    // Return meaningful response
    return interfaceNamesThatSupportWirelessExtensions.get(0);
  }
}
