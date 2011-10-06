package com.polopoly.ps.tools.collections;

import static com.polopoly.util.Require.require;

import java.util.Map.Entry;

import com.polopoly.util.Require;

public class DefaultEntry<K, V> implements Entry<K, V> {
	private K key;
	private V value;

	public DefaultEntry(K key, V value) {
		this.key = require(key);
		this.value = require(value);
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		try {
			return this.value;
		} finally {
			this.value = Require.require(value);
		}
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Entry && ((Entry<?, ?>) o).getKey().equals(key)
				&& ((Entry<?, ?>) o).getValue().equals(value);
	}

	@Override
	public int hashCode() {
		return key.hashCode() * 17 + value.hashCode();
	}

	@Override
	public String toString() {
		return "<" + key + "," + value + ">";
	}
}
