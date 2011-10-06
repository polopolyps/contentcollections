package com.polopoly.ps.tools.collections.componentcollection;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.ps.tools.collections.ComponentStorage;
import com.polopoly.ps.tools.collections.converter.BooleanConverter;
import com.polopoly.ps.tools.collections.converter.StringConverter;
import com.polopoly.ps.tools.collections.exception.NoSuchCollectionException;
import com.polopoly.ps.tools.collections.exception.NoSuchComponentException;
import com.polopoly.ps.tools.collections.incontent.ComponentCollectionProvider;
import com.polopoly.ps.tools.collections.util.FilteringIterator;
import com.polopoly.ps.tools.collections.util.IteratorToSet;
import com.polopoly.util.Require;
import com.polopoly.util.collection.FetchingIterator;
import com.polopoly.util.content.ContentUtil;

public abstract class AbstractProvider<W, T> implements
		ComponentCollectionProvider<W> {
	private static final Logger LOGGER = Logger
			.getLogger(AbstractProvider.class.getName());

	public static final String SET_COMPONENT = "set";

	protected ComponentStorage<Boolean> setStorage;
	protected ComponentStorage<T> storage;

	private ComponentStorage<String> nakedStorage;

	public AbstractProvider(ComponentStorage<T> storage) {
		this.storage = Require.require(storage);
		this.nakedStorage = storage
				.getOtherwiseTypedStorage(new StringConverter());
		this.setStorage = storage
				.getOtherwiseTypedStorage(new BooleanConverter());
	}

	public ComponentStorage<?> getStorage() {
		return storage;
	}

	@Override
	public Iterator<W> values(final ContentUtil content) {
		return new FetchingIterator<W>() {
			private Iterator<String> delegate = keys(content);

			@Override
			protected W fetch() {
				if (!delegate.hasNext()) {
					return null;
				}

				String key = delegate.next();

				try {
					return get(key, content);
				} catch (NoSuchCollectionException e) {
					LOGGER.log(Level.WARNING, "The object " + key
							+ " in the keys of " + this + " did not exist: "
							+ e.getMessage(), e);

					return fetch();
				}
			}
		};

	}

	protected abstract W getExisting(String key, ContentUtil content);

	@Override
	public W get(String key, ContentUtil content)
			throws NoSuchCollectionException {
		try {
			setStorage.getComponent(content, key, SET_COMPONENT);
		} catch (NoSuchComponentException e) {
			throw new NoSuchCollectionException("In " + this + ": " + e, e);
		}

		return getExisting(key, content);
	}

	@Override
	public W create(String key, ContentUtil content) {
		setStorage.setComponent(content, key, SET_COMPONENT, true);

		return getExisting(key, content);
	}

	@Override
	public void clear(String key, ContentUtil content) {
		for (Iterator<String> iterator = storage.components(content, key); iterator
				.hasNext();) {
			String innerKey = iterator.next();

			storage.clearComponent(content, key, innerKey);
		}
	}

	@Override
	public void move(String key, String toKey, ContentUtil content) {
		Set<String> components = IteratorToSet.toSet(storage.components(
				content, key));

		for (String innerKey : components) {
			try {
				nakedStorage.setComponent(content, toKey, innerKey,
						nakedStorage.getComponent(content, key, innerKey));
			} catch (NoSuchComponentException e) {
				LOGGER.log(Level.WARNING, "Could not get component " + innerKey
						+ " returned by iterator in " + content + ": " + e);
			}

			storage.clearComponent(content, key, innerKey);
		}
	}

	@Override
	public Iterator<String> keys(final ContentUtil content) {
		return new FilteringIterator<String>(storage.groups(content)) {

			@Override
			protected boolean isIncluded(String key) {
				try {
					return setStorage.getComponent(content, key, SET_COMPONENT);
				} catch (NoSuchComponentException e) {
					return false;
				}
			}
		};
	}

	@Override
	public String toString() {
		return storage.toString();
	}
}
