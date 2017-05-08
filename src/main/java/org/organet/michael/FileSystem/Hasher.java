package org.organet.michael.FileSystem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hasher {
  public static String calculateFileHash(String path) throws NoSuchAlgorithmException {
    FileInputStream fis;
    try {
      fis = new FileInputStream(path);
    } catch (FileNotFoundException e) {
      return null;
    }

    // Every implementation of the Java platform is required to support the following standard MessageDigest algorithm
    // @see http://docs.oracle.com/javase/8/docs/api/java/security/MessageDigest.html
    MessageDigest md = MessageDigest.getInstance("SHA-256");

    byte[] dataBytes = new byte[1024];

    int nread;
    try {
      while ((nread = fis.read(dataBytes)) != -1) {
        md.update(dataBytes, 0, nread);
      }
    } catch (IOException e) {
      return null;
    }
    byte[] mdbytes = md.digest();

    //convert the byte to hex format method 1
    StringBuilder sb = new StringBuilder();
    for (byte mdbyte : mdbytes) {
      sb.append(Integer.toString((mdbyte & 0xff) + 0x100, 16).substring(1));
    }

    return sb.toString();
  }
}
