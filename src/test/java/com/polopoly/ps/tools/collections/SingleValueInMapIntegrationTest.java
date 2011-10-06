package com.polopoly.ps.tools.collections;

import junit.framework.Assert;

import org.junit.Test;

import com.polopoly.cm.client.CMException;
import com.polopoly.ps.tools.collections.component.DefaultComponentStorage;
import com.polopoly.ps.tools.collections.componentcollection.SingleValueStorage;
import com.polopoly.ps.tools.collections.componentcollection.SingleValueProvider;
import com.polopoly.ps.tools.collections.converter.StringConverter;
import com.polopoly.ps.tools.collections.exception.NoSuchEntryException;
import com.polopoly.ps.tools.collections.exception.NoValueSetException;
import com.polopoly.ps.tools.collections.incontent.ComponentCollectionProvider;
import com.polopoly.ps.tools.collections.incontent.DefaultMapInContent;
import com.polopoly.ps.tools.collections.incontent.EditableMapInContent;
import com.polopoly.ps.tools.collections.incontent.MapInContent;
import com.polopoly.util.exception.PolicyModificationException;
import com.polopoly.util.policy.PolicyModification;

public class SingleValueInMapIntegrationTest extends
		AbstractContentInMapIntegrationTest<SingleValueStorage<String>> {

	@Override
	protected MapInContent<SingleValueStorage<String>> createMap() {
		ComponentCollectionProvider<SingleValueStorage<String>> componentCollectionProvider = new SingleValueProvider<String>(
				new DefaultComponentStorage<String>(new StringConverter()));

		return new DefaultMapInContent<SingleValueStorage<String>>(
				policy, componentCollectionProvider);
	}

	@Test
	public void testSetGet() throws NoValueSetException, NoSuchEntryException,
			PolicyModificationException {
		map = map.modify(createTwoEntriesOneValue());

		Assert.assertEquals("value", map.get("hej").get());

		try {
			map.get("bar").get();

			Assert.fail("Could get non-existing value.");
		} catch (NoValueSetException e) {
			// expected
		}
	}

	protected PolicyModification<EditableMapInContent<SingleValueStorage<String>>> createTwoEntriesOneValue() {
		return new PolicyModification<EditableMapInContent<SingleValueStorage<String>>>() {

			@Override
			public void modify(
					EditableMapInContent<SingleValueStorage<String>> newVersion)
					throws CMException {
				SingleValueStorage<String> storage = newVersion
						.create("hej");

				storage.put("value");

				newVersion.create("bar");
			}
		};
	}

}
