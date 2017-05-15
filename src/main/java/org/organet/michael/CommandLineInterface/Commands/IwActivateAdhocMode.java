package org.organet.michael.CommandLineInterface.Commands;

import java.util.Arrays;

public class IwActivateAdhocMode extends CommandBase {
  public IwActivateAdhocMode(String adhocInterfaceName) {
    super("/sbin/iw", Arrays.asList(adhocInterfaceName, "set", "type", "ibss"));
  }
}
