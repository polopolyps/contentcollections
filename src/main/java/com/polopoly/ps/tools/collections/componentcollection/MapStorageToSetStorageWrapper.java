package com.polopoly.ps.tools.collections.componentcollection;

import java.util.Iterator;
import java.util.Set;

import com.polopoly.ps.tools.collections.exception.NoSuchEntryException;
import com.polopoly.ps.tools.collections.util.IteratorToSet;
import com.polopoly.util.Require;

public class MapStorageToSetStorageWrapper<T> implements SetStorage<T> {
	private static final Boolean VALUE = Boolean.TRUE;
	private MapStorage<T, Boolean> delegate;

	public MapStorageToSetStorageWrapper(MapStorage<T, Boolean> delegate) {
		this.delegate = Require.require(delegate);
	}

	@Override
	public boolean contains(T value) {
		try {
			delegate.get(value);

			return true;
		} catch (NoSuchEntryException e) {
			return false;
		}
	}

	@Override
	public Set<T> asSet() {
		return IteratorToSet.toSet(delegate.keys());
	}

	@Override
	public Iterator<T> iterator() {
		return delegate.keys();
	}

	@Override
	public void add(T... values) {
		for (T value : values) {
			delegate.put(value, VALUE);
		}
	}

	@Override
	public void add(Iterable<T> values) {
		for (T value : values) {
			delegate.put(value, VALUE);
		}
	}

	@Override
	public void set(Iterable<T> values) {
		delegate.clear();

		add(values);
	}

	@Override
	public void remove(T value) {
		delegate.clear(value);
	}

	public String toString() {
		StringBuffer result = new StringBuffer(100);

		result.append("{");

		for (T value : this) {
			if (result.length() > 1) {
				result.append(",");
			}

			result.append(value);
		}

		result.append("}");

		return "Set based on " + delegate + ": " + result.toString();
	}

	@Override
	public boolean isEmpty() {
		return delegate.isEmpty();
	}

}
