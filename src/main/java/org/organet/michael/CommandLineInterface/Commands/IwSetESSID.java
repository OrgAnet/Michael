package org.organet.michael.CommandLineInterface.Commands;

import org.organet.michael.Connectivity.IEEE80211bgChannels;

import java.util.Arrays;

public class IwSetESSID extends CommandBase {
  public IwSetESSID(String adhocInterfaceName, String adhocName, int channel) {
    // TODO `channel` MUST be between 1 and 14 (both inclusive)
    super("/sbin/iw", Arrays.asList(
      adhocInterfaceName, "ibss", "join", adhocName, String.valueOf(IEEE80211bgChannels.getFrequencyOfChannel(channel))));
  }
}
