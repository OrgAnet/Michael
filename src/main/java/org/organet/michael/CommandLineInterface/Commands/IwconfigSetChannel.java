package org.organet.michael.CommandLineInterface.Commands;

import org.organet.michael.CommandLineInterface.ReturnsResponse;
import org.organet.michael.CommandLineInterface.SetsIEEE80211bgChannel;

import java.util.Arrays;

public class IwconfigSetChannel extends CommandBase implements SetsIEEE80211bgChannel, ReturnsResponse {
  public IwconfigSetChannel(String adhocInterfaceName, int channel) {
    super("/sbin/iwconfig", Arrays.asList(adhocInterfaceName, "channel", String.valueOf(channel)));
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
