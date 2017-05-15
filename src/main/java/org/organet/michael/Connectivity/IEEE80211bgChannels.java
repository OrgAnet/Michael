package org.organet.michael.Connectivity;

public class IEEE80211bgChannels {
  // Returns channel center frequency in MHz
  public static int getFrequencyOfChannel(int channel) {
    // TODO Write new exception
    // TODO Throw this new exception if channel less than 1 or greater than 14
    return (2407 + (5 * channel));
  }
}
