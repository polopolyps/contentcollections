package com.polopoly.ps.tools.collections;

import org.junit.Assert;
import org.junit.Test;

import com.polopoly.cm.client.CMException;
import com.polopoly.ps.tools.collections.componentcollection.SetStorage;
import com.polopoly.ps.tools.collections.componentcollection.SetStorageProvider;
import com.polopoly.ps.tools.collections.converter.IntegerConverter;
import com.polopoly.ps.tools.collections.incontent.DefaultListInContent;
import com.polopoly.ps.tools.collections.incontent.EditableListInContent;
import com.polopoly.ps.tools.collections.incontent.ListInContent;
import com.polopoly.util.policy.PolicyModification;

public class SetInListIntegrationTest extends
		AbstractContentInListIntegrationTest<SetStorage<Integer>> {

	@Override
	protected ListInContent<SetStorage<Integer>> createList() {
		SetStorageProvider<Integer> componentCollectionProvider = new SetStorageProvider<Integer>(
				new IntegerConverter());

		return new DefaultListInContent<SetStorage<Integer>>(policy,
				componentCollectionProvider,
				componentCollectionProvider.getStorage());
	}

	@Override
	protected void assertIsEntryWithIndex(int i, SetStorage<Integer> r) {
		if (!r.contains(i)) {
			Assert.fail("Expected the set to contain " + i + "; was " + r);
		}
	}

	@Override
	protected void markEntryAsIndex(int i, SetStorage<Integer> w) {
		w.add(new Integer(i));
	}

	@Test
	public void testAddRemove() throws Exception {
		list = list
				.modify(new PolicyModification<EditableListInContent<SetStorage<Integer>>>() {

					@Override
					public void modify(
							EditableListInContent<SetStorage<Integer>> newVersion)
							throws CMException {
						SetStorage<Integer> set = newVersion.add();

						set.add(1);
						set.add(2);

						set.remove(1);

						Assert.assertTrue(set.contains(2));
						Assert.assertFalse(set.contains(1));

						set.remove(2);

						Assert.assertTrue(set.isEmpty());

						set.remove(3);
					}
				});
	}
}
