package org.organet.michael.FileSystem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.organet.michael.App;
import org.organet.michael.Store.HasID;

import java.io.File;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

// This class is a proxy class which is intended to reflect
// an existing file on the shared directory into the system.
public class SharedFile implements HasID {
  private static final Logger logger = LogManager.getLogger(SharedFile.class.getName());

  private String path; // Shared directory relative local path including filename
  private String filename;
  private long size; // File size in bytes
  private String hash = null;
  private String id; // NDN identifier
  private String type; // audio, document, image or video...
  private List<String> keywords;
  private String name;

  // Local file - directory tree walker invokes this
  SharedFile(String path, long size) throws Exception {
    // Relativize the path if necessary
    this.path = ((Paths.get(App.getSharedDirectoryPath())).relativize(Paths.get(path))).toString();

    this.size = size;

    // Calculate hash
    try {
      hash = Hasher.calculateFileHash(path);
    } catch (NoSuchAlgorithmException e) {
      throw new Exception("Could not calculate the shared file hash.");
    }

    // Calculate ID
    //noinspection ConstantConditions
    id = String.format("/%s/%s/%s", App.getDeviceID(), hash.substring(0, 10), this.path);

    // Decide type via file extension
    filename = Paths.get(path).getFileName().toString();
    String extension = null;
    if (filename.contains(".")) {
      try {
        extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
      } catch (Exception e) {
        logger.warn("Shared file extension could not be obtained therefore its type is going to be set as \"Other\".");

        type = "Other";
      }

      if (extension != null) {
        type = FileTypes.getFileType(extension);
      }
    } else {
      type = "Other";
    }

    keywords = new ArrayList<>();
    keywords.add(type);
  }

  // TODO Remote file - probably connectivity invokes this
  public SharedFile(long size, String id, String hash, List<String> keywords) {
    //
  }

  static SharedFile fromFile(File file) throws Exception {
    return new SharedFile(file.getPath(), file.length());
  }

  public String getPath() {
    return path;
  }

  public String getAbsolutePath() {
    return Paths.get(App.getSharedDirectoryPath(), path).toAbsolutePath().toString();
  }

  String getFilename() {
    return filename;
  }

  public long getSize() {
    return size;
  }

  String getHash() {
    return hash;
  }

  @Override
  public String getID() {
    return id;
  }

  // Keyword can be Java regex string
  public boolean hasKeyword(String keyword) {
    return keywords.contains(keyword);
  }
}
