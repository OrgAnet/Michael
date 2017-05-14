package org.organet.michael.Store;

import java.util.LinkedHashMap;
import java.util.Map;

// NOTE This class will be used to store latest messages
public class LRURepository<T extends HasID> implements Repository<T> {
  // Excerpt from ArrayList
  private static final int DEFAULT_CAPACITY = 10;

  private Map<Integer, T> items;
  private int size; // this is the hard-cap

  public LRURepository() {
    items = new LinkedHashMap<>(DEFAULT_CAPACITY);
  }

  public LRURepository(int size) {
    items = new LinkedHashMap<>(size > 0 ? size : DEFAULT_CAPACITY);
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
    if (size > 0 && items.size() >= size) {
      // TODO Remove one form the bottom (least used)
    }

    // FIXME There SHOULD be same keys exists in the map but instead it overwrites it
    items.put(1, item);
    items.put(1, item);
  }

  @Override
  public T remove(int index) {
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
      // TODO Increment integer of linkedhashmap

      return items.get(itemIndex);
    }
  }
}
