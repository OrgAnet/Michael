package org.organet.michael.CommandLineInterface;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.organet.michael.CommandLineInterface.Commands.*;
import org.organet.michael.Connectivity.AdhocNetwork;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Manager {
  private static final Logger logger = LogManager.getLogger(Manager.class.getName());
  private static final Level VERBOSE = Level.forName("VERBOSE", 550);

  private static final String APP_PASSWD = "muyes5169";
  // Singleton reference
  private static final Manager thisInst = new Manager();

  private String adhocInterfaceName = null;

  // TODO This class will be used for invoking right commands and \
  //      parse their outputs properly under different environments \
  //      (i.e. different operating systems)

  private Manager() {
    // This ctor is private and empty. The reason of these is to
    // hide/avoid the instantiation of this class except itself.

    // WARN Do NOT `run` any CLI command here
  }

  public static Manager getInstance() {
    return thisInst;
  }

  public <C extends CommandBase> String runWithPrivileges(C command) {
    return runWithPrivileges(new ArrayList<>(Arrays.asList(command.getWholeCommand())), command.reportsExitStatus());
  }

  private String runWithPrivileges(List<String> command, boolean reportExitStatus) {
    InputStreamReader input;
    OutputStreamWriter output;

    List<String> sudoCommand = new ArrayList<>(Arrays.asList("sudo", "-S"));
    sudoCommand.addAll(command);

    logger.log(VERBOSE, "Running command: \"{}\"", sudoCommand);

    try {
      // Create the process and start it.
      ProcessBuilder pb = new ProcessBuilder(sudoCommand);
      pb.directory(new File("/"));
      pb.redirectErrorStream(true);
      Process proc = pb.start();

      output = new OutputStreamWriter(proc.getOutputStream());
      input = new InputStreamReader(proc.getInputStream());

      int bytes;
      char buffer[] = new char[1024];
      StringBuilder sb = new StringBuilder();
      while ((bytes = input.read(buffer, 0, 1024)) >= 0) {
        if (bytes == 0) {
          continue;
        }

        //Output the data to console, for debug purposes
        String data = String.valueOf(buffer, 0, bytes);

        // Check for password request
        if (data.contains("[sudo] password")) {
          // Here you can request the password to user using JOPtionPane or System.console().readPassword();
          // I'm just hard coding the password, but in real it's not good.
          char password[] = new char[APP_PASSWD.length()];
          APP_PASSWD.getChars(0, APP_PASSWD.length(), password, 0);
          output.write(password);
          output.write('\n');
          output.flush();

          // erase password data, to avoid security issues.
          Arrays.fill(password, '\0');
        } else {
          sb.append(data);
        }
      }

      // Wait for process to finish...
      while (proc.isAlive()) {
        Thread.sleep(10);
      }
      // ...and then process exit code
      if (proc.exitValue() > 0 && reportExitStatus) {
        System.out.format("Error: Program exited with error code of %d.", proc.exitValue());

        return null;
      }

      return sb.toString();
    } catch (IOException e) {
      e.printStackTrace();
      if (e.getMessage().contains("No such file or directory")) {
        // Command could not be found in the given directory but,
        // this SHOULD NOT be the case since we are checking the
        // commands path upfront.

        logger.fatal("Command could not be found. Terminating...");

        System.exit(1); // TODO Change status code after integration
      }

      return null;
    } catch (InterruptedException e) {
      e.printStackTrace();

      return null;
    }
  }

  public String getAdhocInterfaceName() {
    if (adhocInterfaceName == null) {
      Object response = (new Iwconfig()).getResponse();
      if (response == null) {
        logger.fatal("Could not get ad-hoc network interface name. Terminating...");

        System.exit(1);
        return null;
      }

      adhocInterfaceName = String.valueOf(response);

      logger.info("Ad-hoc network interface name obtained ({}).", adhocInterfaceName);
    }

    return adhocInterfaceName;
  }

  public boolean activateAdhocMode() {
    Object response = (new IwActivateAdhocMode(getAdhocInterfaceName())).getResponse();
    if (response != null && ((boolean) response)) {
      // OK, iw done it

      return true;
    }

    response = (new IwconfigActivateAdhocMode(getAdhocInterfaceName())).getResponse();
    if (response != null && ((boolean) response)) {
      // OK iwconfig done it

      return true;
    }

    return false;
  }

  public boolean setESSID(String essid, int channel) {
    Object response = (new IwSetESSID(getAdhocInterfaceName(), essid, channel)).getResponse();
    if (response != null && ((boolean) response)) {
      // OK, iw done it

      return true;
    }

    // Set channel first and then ESSID
    // "In Ad-Hoc mode, the frequency setting may only be used at initial cell creation, and may be ignored when joining
    // an existing cell." @see https://linux.die.net/man/8/iwconfig
    response = (new IwconfigSetChannel(getAdhocInterfaceName(), channel)).getResponse();
    if (response != null && ((boolean) response)) {
      // OK, iwconfig done it

      response = (new IwconfigSetESSID(getAdhocInterfaceName(), essid)).getResponse();
      if (response != null && ((boolean) response)) {
        return true;
      }

      return false;
    }

    return false;
  }

  public boolean enableNetworkInterface() {
    Object response = (new EnableNetworkInterface(getAdhocInterfaceName())).getResponse();
    if (response == null || (!(boolean) response)) {
      return false;
    }

    return true;
  }

  public List<AdhocNetwork> getAdhocInterface() {
    Object response = (new IwlistScan(getAdhocInterfaceName())).getResponse();
    if (response == null) {
      return null;
    }

    @SuppressWarnings("unchecked") List<Map<String, String>> networkInformations = (List<Map<String, String>>) response;
    if (networkInformations.size() == 0) {
      return null;
    }

    List<AdhocNetwork> networks = new ArrayList<>(networkInformations.size());
//    for (Map<String, String> networkInformation : networkInformations) { // TODO
//      networks.add(new AdhocNetwork(
//        networkInformation.get("address"),
//        networkInformation.get("address"),
//        networkInformation.get("address"),
//        networkInformation.get("address")
//      ));
//    }

    // TODO Maybe introduce another strategy to pick and choose the right interface \
    //      But nonetheless this will work most of the time (e.g. when there is only
    //      one wireless device exists on the system.
    return networks;
  }
}
