package com.polopoly.ps.tools.collections.componentcollection;

import java.util.Set;

public interface ReadOnlySetStorage<T> extends Iterable<T> {
	boolean contains(T value);

	boolean isEmpty();

	Set<T> asSet();
}
