package com.polopoly.ps.tools.collections.componentcollection;

import static com.polopoly.util.Require.require;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.ps.tools.collections.ComponentStorage;
import com.polopoly.ps.tools.collections.componentcollection.MapStorageProvider.Storage;
import com.polopoly.ps.tools.collections.converter.IntegerConverter;
import com.polopoly.ps.tools.collections.exception.NoSuchComponentException;
import com.polopoly.ps.tools.collections.exception.NoSuchEntryException;
import com.polopoly.util.content.ContentUtil;

public class MapStorageToListStorageWrapper<T> implements ListStorage<T> {
	private static final Logger LOGGER = Logger
			.getLogger(MapStorageToListStorageWrapper.class.getName());

	private static final String SIZE_COMPONENT = "size";

	private Storage<Integer, T> delegate;
	private ComponentStorage<Integer> sizeStorage;
	private ContentUtil content;
	private String outerKey;

	public MapStorageToListStorageWrapper(Storage<Integer, T> delegate,
			ComponentStorage<?> storage, ContentUtil content, String outerKey) {
		this.delegate = require(delegate);
		this.sizeStorage = require(storage).getOtherwiseTypedStorage(
				new IntegerConverter());
		this.content = require(content);
		this.outerKey = require(outerKey);
	}

	@Override
	public T get(int index) throws IndexOutOfBoundsException {
		try {
			return delegate.get(index);
		} catch (NoSuchEntryException e) {
			throw new IndexOutOfBoundsException("Attempt to get element "
					+ index + " in " + this + " (size is " + size() + "): "
					+ e.toString());
		}
	}

	@Override
	public int size() {
		try {
			return sizeStorage.getComponent(content, outerKey, SIZE_COMPONENT);
		} catch (NoSuchComponentException e) {
			return 0;
		}
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			int i = 0;
			int size = size();

			@Override
			public boolean hasNext() {
				return i < size;
			}

			@Override
			public T next() {
				if (!hasNext()) {
					throw new IllegalStateException();
				}

				return get(i++);
			}

			@Override
			public void remove() {
				MapStorageToListStorageWrapper.this.remove(i - 1);
			}
		};
	}

	@Override
	public void add(int index, T value) {
		if (value == null) {
			LOGGER.log(Level.WARNING, "Attempt to add null value.");
			return;
		}

		int size = size();

		for (int i = index; i < size; i++) {
			put(i + 1, get(i));
		}

		put(index, value);

		setSize(size + 1);
	}

	private void setSize(int newSize) {
		sizeStorage.setComponent(content, outerKey, SIZE_COMPONENT, newSize);
	}

	private void put(int index, T value) {
		delegate.put(index, value);
	}

	@Override
	public void add(T value) {
		add(size(), value);
	}

	@Override
	public void remove(int index) {
		int oldSize = size();

		for (int i = index + 1; i < oldSize; i++) {
			put(i - 1, get(i));
		}

		delegate.clear(oldSize - 1);

		setSize(oldSize - 1);
	}

	public String toString() {
		StringBuffer result = new StringBuffer(100);

		result.append("[");

		for (T value : this) {
			if (result.length() > 1) {
				result.append(",");
			}

			result.append(value);
		}

		result.append("]");

		return "List in " + outerKey + " in " + content + ": "
				+ result.toString();
	}
}
