/*******************************************************************************
 * Copyright (C) 2018 Kevin Holloway (kholloway@pennyledger.org)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.plcore.util;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DualAccessMap<K, V> implements Map<K, V> {

  private static class ListSet<K> extends ArrayList<K> implements Set<K> {

    private static final long serialVersionUID = 1L;

  }
  
  private ListSet<K> keys = new ListSet<K>();
  private List<V> values = new ArrayList<V>();

  @Override
  public void clear() {
    keys.clear();
    values.clear();
  }
  
  @Override
  public boolean containsKey(Object key) {
    return keys.contains(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return values.contains(value);
  }

  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet() {
    Set<Entry<K, V>> set = new HashSet<Entry<K, V>>();
    for (int i = 0; i < keys.size(); i++) {
      set.add(new SimpleImmutableEntry<K, V>(keys.get(i), values.get(i)));
    }
    return set;
  }
  
  @Override
  public V get(Object key) {
    int n = keys.indexOf(key);
    if (n == -1) {
      return null;
    }
    return values.get(n);
  }

  @Override
  public boolean isEmpty() {
    return keys.isEmpty();
  }

  @Override
  public Set<K> keySet() {
    return keys;
  }

  @Override
  public V put(K key, V value) {
    int n = keys.indexOf(key);
    if (n == -1) {
      keys.add(key);
      values.add(value);
    } else {
      keys.set(n, key);
      values.set(n, value);
    }
    return value;
  }
  
  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public V remove(Object key) {
    int n = keys.indexOf(key);
    if (n != -1) {
      keys.remove(n);
      return values.remove(n);
    } else {
      return null;
    }
  }
  
  @Override
  public int size() {
    return keys.size();
  }

  @Override
  public List<V> values() {
    return values;
  }
  
  
  public K getKey (V value) {
    int n = values.indexOf(value);
    if (n == -1) {
      return null;
    }
    return keys.get(n);
  }
}
