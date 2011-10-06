package com.polopoly.ps.tools.collections.componentcollection;

import static com.polopoly.util.Require.require;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.polopoly.ps.tools.collections.ComponentStorage;
import com.polopoly.ps.tools.collections.Converter;
import com.polopoly.ps.tools.collections.DefaultEntry;
import com.polopoly.ps.tools.collections.exception.ConversionException;
import com.polopoly.ps.tools.collections.exception.NoSuchComponentException;
import com.polopoly.ps.tools.collections.exception.NoSuchEntryException;
import com.polopoly.ps.tools.collections.incontent.ComponentCollectionProvider;
import com.polopoly.ps.tools.collections.util.FilteringIterator;
import com.polopoly.util.collection.TransformingIterator;
import com.polopoly.util.content.ContentUtil;

public class MapStorageProvider<K, V> extends
		AbstractProvider<MapStorage<K, V>, V> implements
		ComponentCollectionProvider<MapStorage<K, V>> {
	public static final String QUOTE = "\\";

	private Converter<K> keyConverter;

	static final class Storage<K, V> implements MapStorage<K, V> {
		private final ContentUtil content;
		private final String outerKey;
		private final ComponentStorage<V> storage;
		private final Converter<K> keyConverter;

		Storage(ComponentStorage<V> storage, Converter<K> keyConverter,
				ContentUtil content, String outerKey) {
			this.storage = storage;
			this.keyConverter = keyConverter;

			this.content = content;
			this.outerKey = outerKey;
		}

		@Override
		public V get(K innerKey) throws NoSuchEntryException {
			try {
				return storage.getComponent(content, outerKey,
						getComponent(innerKey));
			} catch (NoSuchComponentException e) {
				throw new NoSuchEntryException(e);
			}
		}

		@Override
		public void put(K innerKey, V value) {
			if (value == null) {
				clear(innerKey);
			} else {
				storage.setComponent(content, outerKey,
						getComponent(require(innerKey)), require(value));
			}
		}

		@Override
		public void clear(K innerKey) {
			storage.clearComponent(content, outerKey,
					getComponent(require(innerKey)));
		}

		/**
		 * Because of the set component, we cannot name any component "set".
		 * Otherwise, we use the same component names as keys.
		 */
		private String getComponent(K innerKey) {
			String component = keyConverter.toString(innerKey);

			if (component.startsWith(SET_COMPONENT)
					|| component.startsWith(QUOTE)) {
				return QUOTE + component;
			} else {
				return component;
			}
		}

		/**
		 * Because of the set component, we cannot name any component "set".
		 * Otherwise, we use the same component names as keys.
		 */
		private String getKey(String component) {
			if (require(component).startsWith(QUOTE)) {
				return component.substring(QUOTE.length());
			} else {
				return component;
			}
		}

		@Override
		public boolean equals(Object o) {
			return o.getClass().equals(getClass())
					&& outerKey.equals(((Storage<?, ?>) o).outerKey)
					&& content.getContentId().equalsIgnoreVersion(
							((Storage<?, ?>) o).content.getContentId());
		}

		@Override
		public int hashCode() {
			return content.getContentId().unversioned().hashCode() * 7
					+ outerKey.hashCode();
		}

		@Override
		public Iterator<K> keys() {
			return new TransformingIterator<String, K>(
					new FilteringIterator<String>(storage.components(content,
							outerKey)) {
						@Override
						protected boolean isIncluded(String value) {
							return !value.equals(SET_COMPONENT);
						}

						@Override
						protected String transform(String component) {
							return getKey(component);
						}
					}) {

				@Override
				protected K transform(String next) {
					try {
						return keyConverter.fromString(next);
					} catch (ConversionException e) {
						throw new RuntimeException(e);
					}
				}

			};
		}

		@Override
		public Iterator<Entry<K, V>> iterator() {
			return new TransformingIterator<K, Map.Entry<K, V>>(keys()) {

				@Override
				protected Entry<K, V> transform(K next) {
					try {
						return new DefaultEntry<K, V>(next, get(next));
					} catch (NoSuchEntryException e) {
						throw new RuntimeException("Internal error: "
								+ e.getMessage(), e);
					}
				}
			};
		}

		@Override
		public void clear() {
			Iterator<String> it = storage.components(content, outerKey);

			while (it.hasNext()) {
				storage.clearComponent(content, outerKey, it.next());
			}
		}

		@Override
		public String toString() {
			StringBuffer result = new StringBuffer(100);

			result.append("{");

			for (Entry<K, V> value : this) {
				if (result.length() > 1) {
					result.append(",");
				}

				result.append(value.getKey());
				result.append(':');
				result.append(value.getValue());
			}

			result.append("}");

			return "Map in " + outerKey + " in " + content + ": "
					+ result.toString();
		}

		@Override
		public boolean isEmpty() {
			return !keys().hasNext();
		}
	}

	public MapStorageProvider(ComponentStorage<V> storage,
			Converter<K> keyConverter) {
		super(storage);

		this.keyConverter = require(keyConverter);
	}

	@Override
	public MapStorage<K, V> getExisting(final String outerKey,
			final ContentUtil content) {
		return new Storage<K, V>(storage, keyConverter, content, outerKey);
	}

}
