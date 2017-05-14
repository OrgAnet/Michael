package org.organet.michael.Store;

public interface Cache<T extends HasID> {
  void setSize(int size); // 0 means no size limit
  void add(T item);
  T remove(int index);
  T remove(String id);
  T find(String id);

//  public SharedFile findSharedFile(String sharedFileID) throws FileNotFoundException {
//    // TODO
//
//    throw new FileNotFoundException(String.format("The shared file could not be found in the local repository with " +
//      "the given ID (%s).", sharedFileID));
//  }
}
