package org.organet.michael.CommandLineInterface.Commands;

import java.util.Arrays;

public class IpSetAddress extends CommandBase {
  public IpSetAddress(String adhocInterfaceName, String ip) {
    super("/sbin/ip", Arrays.asList("addr", "add", ip, "dev", adhocInterfaceName));
  }
}
