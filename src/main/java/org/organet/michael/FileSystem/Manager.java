package org.organet.michael.FileSystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.organet.michael.App;
import org.organet.michael.Connectivity.Messages.AdhocMessage;
import org.organet.michael.Connectivity.Messages.FILE_SYSTEM.ReceiveMessage;
import org.organet.michael.Connectivity.Messages.ReflectionName;
import org.organet.michael.Connectivity.Node;
import org.organet.michael.ManagerBase;
import org.organet.michael.Store.DefaultRepository;
import org.organet.michael.Store.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Manager extends ManagerBase {
  private static final Logger logger = LogManager.getLogger(Manager.class.getName());
  // Singleton reference
  private static final Manager thisInst = new Manager();

  private org.organet.michael.Store.Manager storeManager = org.organet.michael.Store.Manager.getInstance();

  private Watcher watcher;
  private Repository<SharedFile> localRepositoryInst;

  private Manager() {
    // This ctor is private and empty. The reason of these is to
    // hide/avoid the instantiation of this class except itself.

    // TODO Get local (and remote?) repos from storeManager
    localRepositoryInst = storeManager.getLocalRepository();

    // Walk shared directory for initial indexing
    try(Stream<Path> paths = Files.walk(Paths.get(App.getSharedDirectoryPath()))) {
      paths.forEach(filePath -> {
        if (Files.isRegularFile(filePath)) {
          // TODO Check if this shared file has a record in the local repository DB \
          //      If so check the hashes, if differs delete record in the DB
          try {
            SharedFile sharedFile = SharedFile.fromFile(filePath.toFile());
            // TODO [1ST] Store this shared file in local repository (obviously)
            localRepositoryInst.add(sharedFile);
          } catch (Exception e) {
            logger.warn("Could not add shared file ({}) to repository. Moving to next (if any)...", filePath);

            return;
          }

//          localIndex.add(sharedFile); // TODO Add it to local repository DB
        }
      });
    } catch (IOException e) {
      e.printStackTrace(); // FIXME
    }
  }

  public static Manager getInstance() {
    return thisInst;
  }

  public void start(String sharedDirectory) {
    try {
      watcher = new Watcher(sharedDirectory);
    } catch (IOException e) {
      e.printStackTrace(); // FIXME

      return;
    }

    // NOTE Threads can be controlled if stored
    (new Thread(watcher)).start();
  }

  Repository<SharedFile> getLocalRepositoryInst() {
    return localRepositoryInst;
  }

  @ReflectionName("get")
  private void sendFile(Node node, AdhocMessage message) {
    String sharedFileID = message.getArguments().toString();

    // TODO Resolve shared file via its ID
    DefaultRepository localRepository = org.organet.michael.Store.Manager.getInstance().getLocalRepository();
    SharedFile sharedFile = (SharedFile) localRepository.find(sharedFileID);

    if (sharedFile == null) {
      // This means once this node has the file and other nodes informed with this information. But
      // then file is gone somehow but other nodes' remote repository still has the shared file record
      // as if it is still in this very node. Therefore other nodes' remote repository MUST delete
      // that record. Also a "not found" message as reply to the interest node would be polite.
      logger.error("Promised file could not be found in the local repository. Interests will be informed.");

      // TODO Reply with "not found" or something similar
      return;
    }

    logger.info("Sending the file with the ID of {} and name of {}...", sharedFileID, sharedFile.getFilename());

    node.send(new ReceiveMessage(sharedFile.getSize()));
    node.send(sharedFile);
  }
}
