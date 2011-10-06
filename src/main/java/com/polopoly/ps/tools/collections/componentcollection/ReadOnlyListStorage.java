package com.polopoly.ps.tools.collections.componentcollection;

public interface ReadOnlyListStorage<T> extends Iterable<T> {
	T get(int index) throws IndexOutOfBoundsException;

	int size();

	boolean isEmpty();
}
