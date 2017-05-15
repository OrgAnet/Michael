package org.organet.michael.CommandLineInterface.Commands;

import org.organet.michael.CommandLineInterface.Manager;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandBase {
  private String command;
  private List<String> arguments;
  private boolean reportExitStatus = false;

  CommandBase(String command) {
    this.command = command;
    arguments = new ArrayList<>(0);
  }

  CommandBase(String command, List<String> arguments) {
    this.command = command;
    this.arguments = arguments;
  }

  public String[] getWholeCommand() {
    String[] wholeCommand = new String[1 + arguments.size()];

    wholeCommand[0] = command;
    for (int i = 0, len = arguments.size(); i < len; i++) {
      wholeCommand[i + 1] = arguments.get(i);
    }

    return wholeCommand;
  }

  public boolean reportsExitStatus() {
    return reportExitStatus;
  }

  void setReportExitStatus(boolean reportExitStatus) {
    this.reportExitStatus = reportExitStatus;
  }

  public String run() {
    return Manager.getInstance().runWithPrivileges(this);
  }
}
