package com.polopoly.ps.tools.collections.componentcollection;

import com.polopoly.ps.tools.collections.ComponentStorage;
import com.polopoly.ps.tools.collections.exception.NoSuchComponentException;
import com.polopoly.ps.tools.collections.exception.NoValueSetException;
import com.polopoly.ps.tools.collections.incontent.ComponentCollectionProvider;
import com.polopoly.util.content.ContentUtil;

public class SingleValueProvider<T> extends
		AbstractProvider<SingleValueStorage<T>, T> implements
		ComponentCollectionProvider<SingleValueStorage<T>> {
	private final class Storage implements SingleValueStorage<T> {
		private final ContentUtil content;
		private String key;

		private Storage(String key, ContentUtil content) {
			this.content = content;
			this.key = key;
		}

		@Override
		public void put(T value) {
			storage.setComponent(content, key, VALUE_COMPONENT, value);
		}

		@Override
		public T get() throws NoValueSetException {
			try {
				return storage.getComponent(content, key, VALUE_COMPONENT);
			} catch (NoSuchComponentException e) {
				throw new NoValueSetException(e);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(Object o) {
			return o.getClass().equals(getClass())
					&& key.equals(((Storage) o).key)
					&& content.getContentId().equalsIgnoreVersion(
							((Storage) o).content.getContentId());
		}

		@Override
		public int hashCode() {
			return content.getContentId().unversioned().hashCode() * 7
					+ key.hashCode();
		}

		@Override
		public String toString() {
			return "single value storage " + key + " in " + content;
		}

	}

	private static final String VALUE_COMPONENT = "value";

	public SingleValueProvider(ComponentStorage<T> storage) {
		super(storage);
	}

	@Override
	protected SingleValueStorage<T> getExisting(String key,
			ContentUtil content) {
		return new Storage(key, content);
	}
};
