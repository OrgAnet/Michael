package org.organet.michael.CommandLineInterface;

// NOTE This interface is exist so that `iw` or `iwconfig` commands/classes
//      may be used by the application to achieve the same result depending
//      on their shell command exists. So if `iw` shell command could not be
//      found `iwconfig` shell command, thus relevant class that implements
//      for example `ScansForAdhocNetworks` interface will be used
//      interchangeably.
public interface ScansForAdhocNetworks {
}
