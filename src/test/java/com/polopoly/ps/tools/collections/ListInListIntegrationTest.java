package com.polopoly.ps.tools.collections;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import com.polopoly.cm.client.CMException;
import com.polopoly.ps.tools.collections.component.DefaultComponentStorage;
import com.polopoly.ps.tools.collections.componentcollection.ListStorage;
import com.polopoly.ps.tools.collections.componentcollection.ListStorageProvider;
import com.polopoly.ps.tools.collections.converter.IntegerConverter;
import com.polopoly.ps.tools.collections.incontent.DefaultListInContent;
import com.polopoly.ps.tools.collections.incontent.EditableListInContent;
import com.polopoly.ps.tools.collections.incontent.ListInContent;
import com.polopoly.util.policy.PolicyModification;

public class ListInListIntegrationTest extends
		AbstractContentInListIntegrationTest<ListStorage<Integer>> {

	@Override
	protected ListInContent<ListStorage<Integer>> createList() {
		ListStorageProvider<Integer> componentCollectionProvider = new ListStorageProvider<Integer>(
				new DefaultComponentStorage<Integer>(new IntegerConverter()));

		return new DefaultListInContent<ListStorage<Integer>>(policy,
				componentCollectionProvider,
				componentCollectionProvider.getStorage());
	}

	@Override
	protected void assertIsEntryWithIndex(int i, ListStorage<Integer> r) {
		if (!r.get(0).equals(i)) {
			Assert.fail("Expected the list to contain " + i + "; was " + r);
		}
	}

	@Override
	protected void markEntryAsIndex(int i, ListStorage<Integer> w) {
		w.add(new Integer(i));
	}

	@Test
	public void testAddRemove() throws Exception {
		list = list
				.modify(new PolicyModification<EditableListInContent<ListStorage<Integer>>>() {

					@Override
					public void modify(
							EditableListInContent<ListStorage<Integer>> newVersion)
							throws CMException {
						ListStorage<Integer> list = newVersion.add();

						list.add(1);
						list.add(2);

						Assert.assertEquals(2, list.size());

						list.remove(0);

						Assert.assertEquals((Integer) 2, list.get(0));
						Assert.assertEquals(1, list.size());

						list.remove(0);

						Assert.assertTrue(list.isEmpty());
					}
				});
	}

	@Test
	public void testIterator() throws Exception {
		list = list
				.modify(new PolicyModification<EditableListInContent<ListStorage<Integer>>>() {

					@Override
					public void modify(
							EditableListInContent<ListStorage<Integer>> newVersion)
							throws CMException {
						ListStorage<Integer> list = newVersion.add();

						list.add(1);
						list.add(2);

						Iterator<Integer> it = list.iterator();

						Assert.assertTrue(it.hasNext());
						Assert.assertEquals((Integer) 1, it.next());

						Assert.assertTrue(it.hasNext());
						Assert.assertEquals((Integer) 2, it.next());

						Assert.assertFalse(it.hasNext());
					}
				});
	}
}
