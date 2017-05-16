package org.organet.michael.Connectivity;

public class AdhocNetwork {
  private String macAddress;
  private String essid;
  private int frequency; // in MHz
  private int channel; // 1 through 14
  private boolean encrypted;

  public AdhocNetwork(String macAddress, String essid, int frequency, boolean encrypted) {
    this.macAddress = macAddress;
    this.essid = essid;
    this.frequency = frequency;
    this.encrypted = encrypted;
  }

  public String getMacAddress() {
    return macAddress;
  }

  public String getEssid() {
    return essid;
  }

  public int getFrequency() {
    return frequency;
  }

  public int getChannel() {
    return channel;
  }

  public boolean isEncrypted() {
    return encrypted;
  }
}
