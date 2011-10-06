package com.polopoly.ps.tools.collections.componentcollection;

public interface QueueStorage<T> extends Iterable<T> {
	boolean isEmpty();

	int size();

	void push(T value);

	T pop();
}
