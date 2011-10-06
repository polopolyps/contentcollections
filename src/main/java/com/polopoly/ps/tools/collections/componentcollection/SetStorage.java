package com.polopoly.ps.tools.collections.componentcollection;

public interface SetStorage<T> extends ReadOnlySetStorage<T> {
	void add(T... value);

	void add(Iterable<T> values);

	void set(Iterable<T> values);

	void remove(T value);
}
