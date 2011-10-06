package com.polopoly.ps.tools.collections.componentcollection;

public interface SingleValueStorage<T> extends ReadOnlySingleValueStorage<T> {
	void put(T value);
}
