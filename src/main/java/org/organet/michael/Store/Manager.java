package org.organet.michael.Store;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.organet.michael.Connectivity.Messages.ReflectionName;
import org.organet.michael.FileSystem.SharedFile;
import org.organet.michael.ManagerBase;

public class Manager extends ManagerBase {
  private static final Logger logger = LogManager.getLogger(Manager.class.getName());
  // Singleton reference
  private static final Manager thisInst = new Manager();

  private DefaultRepository<SharedFile> localRepository;
  private DefaultRepository<SharedFile> remoteRepository;

  private Manager() {
    // This ctor is private and empty. The reason of these is to
    // hide/avoid the instantiation of this class except itself.

    localRepository = new DefaultRepository<>();
    remoteRepository = new DefaultRepository<>(10);
  }

  public static Manager getInstance() {
    return thisInst;
  }

  public DefaultRepository<SharedFile> getLocalRepository() {
    return localRepository;
  }

  @ReflectionName("get")
  private void getLocalIndex(Object count) {
    // TODO Return whole (`count` == null) or partial (first `count` items) items in local index
  }
}
