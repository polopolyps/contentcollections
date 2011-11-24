package com.polopoly.ps.tools.collections;

import static com.polopoly.ps.tools.collections.util.IteratorToSet.toSet;
import static com.polopoly.util.policy.Util.util;

import java.util.Iterator;
import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.polopoly.cm.client.CMException;
import com.polopoly.ps.service.NoSuchServiceException;
import com.polopoly.ps.test.AbstractIntegrationTest;
import com.polopoly.ps.test.client.ClientInitializer;
import com.polopoly.ps.test.client.PolopolyTestClientInitializer;
import com.polopoly.ps.tools.collections.exception.NoSuchEntryException;
import com.polopoly.ps.tools.collections.exception.NoValueSetException;
import com.polopoly.ps.tools.collections.incontent.EditableMapInContent;
import com.polopoly.ps.tools.collections.incontent.MapInContent;
import com.polopoly.util.exception.PolicyModificationException;
import com.polopoly.util.policy.PolicyModification;
import com.polopoly.util.policy.PolicyUtil;

public abstract class AbstractContentInMapIntegrationTest<W>
		extends AbstractIntegrationTest {
	protected MapInContent<W> map;
	protected PolicyUtil policy;

	@Override
	protected ClientInitializer getInitializer() throws NoSuchServiceException {
		PolopolyTestClientInitializer result = new PolopolyTestClientInitializer();
		
		result.setAttachSolr(false);
		
		return result;
	}

	@Before
	public void setUp() throws Exception {
		policy = util(context.createPolicy(17, "p.DefaultAppConfig"));

		map = createMap();
	}

	protected abstract MapInContent<W> createMap();

	@After
	public void tearDown() throws Exception {
		context.getPolicyCMServer().removeContent(
				policy.getContentId().unversioned());
	}

	@Test
	public void testGetNonExisting() {
		try {
			map.get("nonExistingKey");

			Assert.fail("Could get non-existing entry.");
		} catch (NoSuchEntryException e) {
			// expected
		}
	}

	@Test
	public void testCreateEntry() throws Exception {
		map.modify(createCreatingModification("a"));
		map.get("a");

		try {
			map.get("b");

			Assert.fail("Could get non-existing entry.");
		} catch (NoSuchEntryException e) {
			// expected
		}
	}

	@Test
	public void testClearEntry() throws Exception {
		map = map.modify(createCreatingModification("a"));
		map = map.modify(createClearingModification("a"));

		try {
			map.get("a");

			Assert.fail("Could get clared entry.");
		} catch (NoSuchEntryException e) {
			// expected
		}
	}

	@Test
	public void testIterators() throws NoValueSetException,
			NoSuchEntryException, PolicyModificationException {
		map = map.modify(createCreatingModification("a"));
		map = map.modify(createCreatingModification("b"));

		assertIteratorContainsOnly(map.iterator(), map.get("a"), map.get("b"));
		assertIteratorContainsOnly(map.keys(), "a", "b");
	}

	protected PolicyModification<EditableMapInContent<W>> createCreatingModification(
			final String key) {
		return new PolicyModification<EditableMapInContent<W>>() {

			@Override
			public void modify(EditableMapInContent<W> newVersion)
					throws CMException {
				newVersion.create(key);
			}
		};
	}

	protected PolicyModification<EditableMapInContent<W>> createClearingModification(
			final String key) {
		return new PolicyModification<EditableMapInContent<W>>() {

			@Override
			public void modify(EditableMapInContent<W> newVersion)
					throws CMException {
				newVersion.clear(key);
			}
		};
	}

	<T> void assertIteratorContainsOnly(Iterator<T> iterator, T... values) {
		Set<T> set = toSet(iterator);

		for (T value : values) {
			Assert.assertTrue("Iterator never returned " + value,
					set.contains(value));
		}

		for (T value : set) {
			Assert.assertTrue("Iterator contained " + value
					+ " which it shouldn't", contains(values, value));
		}
	}

	private <T> boolean contains(T[] array, T value) {
		for (T arrayValue : array) {
			if (value.equals(arrayValue)) {
				return true;
			}
		}

		return false;
	}

}
