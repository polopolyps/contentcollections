package com.polopoly.ps.tools.collections;

import static com.polopoly.ps.tools.collections.componentcollection.AbstractProvider.SET_COMPONENT;
import static com.polopoly.ps.tools.collections.componentcollection.MapStorageProvider.QUOTE;
import junit.framework.Assert;

import org.junit.Test;

import com.polopoly.cm.client.CMException;
import com.polopoly.ps.tools.collections.component.DefaultComponentStorage;
import com.polopoly.ps.tools.collections.componentcollection.MapStorage;
import com.polopoly.ps.tools.collections.componentcollection.ReadOnlyMapStorage;
import com.polopoly.ps.tools.collections.componentcollection.MapStorageProvider;
import com.polopoly.ps.tools.collections.converter.StringConverter;
import com.polopoly.ps.tools.collections.exception.NoSuchEntryException;
import com.polopoly.ps.tools.collections.exception.NoValueSetException;
import com.polopoly.ps.tools.collections.incontent.ComponentCollectionProvider;
import com.polopoly.ps.tools.collections.incontent.DefaultMapInContent;
import com.polopoly.ps.tools.collections.incontent.EditableMapInContent;
import com.polopoly.ps.tools.collections.incontent.MapInContent;
import com.polopoly.util.exception.PolicyModificationException;
import com.polopoly.util.policy.PolicyModification;

public class MapInMapIntegrationTest extends
		AbstractContentInMapIntegrationTest<MapStorage<String, String>> {
	@Override
	protected MapInContent<MapStorage<String, String>> createMap() {
		ComponentCollectionProvider<MapStorage<String, String>> componentCollectionProvider = new MapStorageProvider<String, String>(
				new DefaultComponentStorage<String>(new StringConverter()),
				new StringConverter());

		return new DefaultMapInContent<MapStorage<String, String>>(
				policy, componentCollectionProvider);
	}

	@Test
	public void testClear() throws NoValueSetException, NoSuchEntryException,
			PolicyModificationException {
		map = map
				.modify(new PolicyModification<EditableMapInContent<MapStorage<String, String>>>() {

					@Override
					public void modify(
							EditableMapInContent<MapStorage<String, String>> newVersion)
							throws CMException {
						MapStorage<String, String> storage = newVersion
								.create("noValues");

						storage.put("key", "value");
						storage.put("key2", "value2");

						storage.clear("key");
						storage.put("key2", null);

						ReadOnlyMapStorage<String, String> emptyMap;

						try {
							emptyMap = map.get("noValues");
						} catch (NoSuchEntryException e) {
							throw new CMException(e);
						}

						assertIteratorContainsOnly(emptyMap.keys());

						try {
							emptyMap.get("key");

							Assert.fail("Could get cleared value");
						} catch (NoSuchEntryException e) {
							// expected
						}

					}
				});
	}

	@Test
	public void testGet() throws NoValueSetException, NoSuchEntryException,
			PolicyModificationException {
		map = map
				.modify(new PolicyModification<EditableMapInContent<MapStorage<String, String>>>() {

					@Override
					public void modify(
							EditableMapInContent<MapStorage<String, String>> newVersion)
							throws CMException {
						try {
							MapStorage<String, String> storage = newVersion
									.create("twoValues");

							storage.put("key", "value");
							storage.put("key2", "value2");

							ReadOnlyMapStorage<String, String> twoValuedMap = map
									.get("twoValues");

							Assert.assertEquals("value",
									twoValuedMap.get("key"));
							Assert.assertEquals("value2",
									twoValuedMap.get("key2"));

							newVersion.create("noValues");

							ReadOnlyMapStorage<String, String> bar = map
									.get("noValues");

							try {
								bar.get("key");

								Assert.fail("Could get non-existing value.");
							} catch (NoSuchEntryException e) {
								// expected
							}
						} catch (Exception e) {
							throw new CMException(e);
						}
					}
				});
	}

	@Test
	public void testKeysAndValues() throws NoValueSetException,
			NoSuchEntryException, PolicyModificationException {
		map = map
				.modify(new PolicyModification<EditableMapInContent<MapStorage<String, String>>>() {

					@Override
					public void modify(
							EditableMapInContent<MapStorage<String, String>> newVersion)
							throws CMException {
						try {
							MapStorage<String, String> storage = newVersion
									.create("twoValues");

							storage.put("key", "value");
							storage.put("key2", "value2");

							ReadOnlyMapStorage<String, String> twoValuedMap = map
									.get("twoValues");

							assertIteratorContainsOnly(twoValuedMap.keys(),
									"key", "key2");
							assertIteratorContainsOnly(twoValuedMap.iterator(),
									new DefaultEntry<String, String>("key", "value"),
									new DefaultEntry<String, String>("key2", "value2"));

							newVersion.create("noValues");

							ReadOnlyMapStorage<String, String> emptyMap = map
									.get("noValues");

							assertIteratorContainsOnly(emptyMap.keys());
							assertIteratorContainsOnly(emptyMap.iterator());
						} catch (Exception e) {
							throw new CMException(e);
						}
					}
				});
	}

	/**
	 * The key named "set" is handled differently from others.
	 */
	@Test
	public void testSetAsKey() throws NoValueSetException,
			NoSuchEntryException, PolicyModificationException {
		map = map
				.modify(new PolicyModification<EditableMapInContent<MapStorage<String, String>>>() {

					@Override
					public void modify(
							EditableMapInContent<MapStorage<String, String>> newVersion)
							throws CMException {
						MapStorage<String, String> storage = newVersion
								.create("noValues");

						try {
							storage.get(SET_COMPONENT);

							Assert.fail("Could get set value");
						} catch (NoSuchEntryException e) {
							// expected
						}

						try {
							storage.put(SET_COMPONENT, "value");
							Assert.assertEquals("value",
									storage.get(SET_COMPONENT));

							ReadOnlyMapStorage<String, String> emptyMap;

							emptyMap = map.get("noValues");
						} catch (NoSuchEntryException e) {
							throw new CMException(e);
						}

						storage.put(QUOTE + SET_COMPONENT, "value");

						assertIteratorContainsOnly(storage.keys(), QUOTE
								+ SET_COMPONENT, SET_COMPONENT);
					}
				});
	}
}
