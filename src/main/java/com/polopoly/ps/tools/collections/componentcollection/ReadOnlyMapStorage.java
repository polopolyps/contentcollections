package com.polopoly.ps.tools.collections.componentcollection;

import java.util.Iterator;
import java.util.Map.Entry;

import com.polopoly.ps.tools.collections.exception.NoSuchEntryException;

public interface ReadOnlyMapStorage<K, V> extends Iterable<Entry<K, V>> {
	V get(K key) throws NoSuchEntryException;

	Iterator<K> keys();

	boolean isEmpty();
}
