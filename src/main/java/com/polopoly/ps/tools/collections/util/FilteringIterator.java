package com.polopoly.ps.tools.collections.util;

import static com.polopoly.util.Require.require;

import java.util.Iterator;

import com.polopoly.util.collection.FetchingIterator;

public abstract class FilteringIterator<T> extends FetchingIterator<T> {
	private Iterator<T> delegate;

	public FilteringIterator(Iterator<T> delegate) {
		this.delegate = require(delegate);
	}

	@Override
	protected T fetch() {
		while (delegate.hasNext()) {
			T result = delegate.next();

			if (isIncluded(result)) {
				return transform(result);
			}
		}

		return null;
	}

	protected T transform(T value) {
		return value;
	}

	protected abstract boolean isIncluded(T value);
}
