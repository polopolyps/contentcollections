package com.polopoly.ps.tools.collections.componentcollection;

import com.polopoly.ps.tools.collections.exception.NoValueSetException;

public interface ReadOnlySingleValueStorage<T> {
	T get() throws NoValueSetException;
}
