package com.polopoly.ps.tools.collections.componentcollection;

public interface MapStorage<K, V> extends ReadOnlyMapStorage<K, V> {
	void put(K key, V value);

	void clear(K key);

	void clear();
}
