package com.polopoly.ps.tools.collections.componentcollection;

public interface ListStorage<T> extends ReadOnlyListStorage<T> {

	void add(int index, T value);

	void add(T value);

	void remove(int index);

}
