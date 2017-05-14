package org.organet.michael.Store;

import java.util.ArrayList;
import java.util.List;

// NOTE This class will be used to store local (size=0) and remote (size>0) shared files
public class DefaultRepository<T extends HasID> implements Repository<T> {
  // Excerpt from ArrayList
  private static final int DEFAULT_CAPACITY = 10;

  private List<T> items;
  private int size = 0; // 0 means limitless (in theory)

  DefaultRepository() {
    items = new ArrayList<>();
  }

  DefaultRepository(int size) {
    items = new ArrayList<>(size > 0 ? size : DEFAULT_CAPACITY);
    this.size = size;
  }

  @Override
  public void setSize(int size) {
    int trimLastNItems = this.size - size;
    this.size = size;

    // Do trimming if size set is less than currently existing count
    if (trimLastNItems > 0) {
      // TODO
    }
  }

  @Override
  public void add(T item) {
    items.add(item);
  }

  @Override
  public T remove(int index) throws IndexOutOfBoundsException {
    return items.remove(index);
  }

  private int getIndexOf(String id) {
    for (int i = 0, len = items.size(); i < len; i++) {
      if (items.get(i).getID().equals(id)) {
        return i;
      }
    }

    return -1;
  }

  private int getIndexOf(T item) {
    return getIndexOf(item.getID());
  }

  @Override
  public T remove(String id) {
    int itemIndex = getIndexOf(id);

    if (itemIndex == -1) {
      return null;
    } else {
      return items.remove(itemIndex);
    }
  }

  @Override
  public T find(String id) {
    int itemIndex = getIndexOf(id);

    if (itemIndex == -1) {
      return null;
    } else {
      return items.get(itemIndex);
    }
  }
}
