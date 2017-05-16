package org.organet.michael.CommandLineInterface.Commands;

import org.organet.michael.CommandLineInterface.ReturnsResponse;
import org.organet.michael.CommandLineInterface.SetsESSID;
import org.organet.michael.CommandLineInterface.SetsIEEE80211bgChannel;
import org.organet.michael.Connectivity.IEEE80211bgChannels;

import java.util.Arrays;

public class IwSetESSID extends CommandBase implements SetsESSID, SetsIEEE80211bgChannel, ReturnsResponse {
  public IwSetESSID(String adhocInterfaceName, String essid, int channel) {
    // TODO `channel` MUST be between 1 and 14 (both inclusive)
    super("/sbin/iw", Arrays.asList(
      adhocInterfaceName, "ibss", "join", essid, String.valueOf(IEEE80211bgChannels.getFrequencyOfChannel(channel))));
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
