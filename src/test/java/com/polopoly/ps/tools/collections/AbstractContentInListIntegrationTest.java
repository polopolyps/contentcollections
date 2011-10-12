package com.polopoly.ps.tools.collections;

import static com.polopoly.util.policy.Util.util;

import java.util.Iterator;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.polopoly.cm.client.CMException;
import com.polopoly.ps.test.AbstractIntegrationTest;
import com.polopoly.ps.test.client.ClientInitializer;
import com.polopoly.ps.test.client.NoSuchServiceException;
import com.polopoly.ps.test.client.PolopolyTestClientInitializer;
import com.polopoly.ps.tools.collections.exception.NoSuchEntryException;
import com.polopoly.ps.tools.collections.exception.NoValueSetException;
import com.polopoly.ps.tools.collections.incontent.EditableListInContent;
import com.polopoly.ps.tools.collections.incontent.ListInContent;
import com.polopoly.util.exception.PolicyModificationException;
import com.polopoly.util.policy.PolicyModification;
import com.polopoly.util.policy.PolicyUtil;

public abstract class AbstractContentInListIntegrationTest<W> extends
		AbstractIntegrationTest {
	protected ListInContent<W> list;
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

		list = createList();
	}

	protected abstract ListInContent<W> createList();

	@After
	public void tearDown() throws Exception {
		context.getPolicyCMServer().removeContent(
				policy.getContentId().unversioned());
	}

	@Test
	public void testGetNonExisting() {
		try {
			list.get(0);

			Assert.fail("Could get non-existing entry.");
		} catch (IndexOutOfBoundsException e) {
			// expected
		}

		try {
			list.get(-1);

			Assert.fail("Could get non-existing entry.");
		} catch (IndexOutOfBoundsException e) {
			// expected
		}
	}

	@Test
	public void testCreateEntry() throws Exception {
		list = list.modify(createAddingModification(0, 0));
		list.get(0);

		try {
			list.get(1);

			Assert.fail("Could get non-existing entry.");
		} catch (IndexOutOfBoundsException e) {
			// expected
		}
	}

	@Test
	public void testAddEntry() throws Exception {
		list = list.modify(createAddingModification(0, 0));
		list = list.modify(createAddingModification(1, 2));
		list = list.modify(createAddingModification(1, 1));

		System.out.println(list);

		assertIsEntryWithIndex(0, list.get(0));
		assertIsEntryWithIndex(1, list.get(1));
		assertIsEntryWithIndex(2, list.get(2));
	}

	@Test
	public void testRemoveEntry() throws Exception {
		list = list.modify(createAddingModification(0, 0));
		list = list.modify(createAddingModification(1, 1));
		list = list.modify(createAddingModification(2, 2));

		list = list.modify(createRemovingModification(0));

		assertIsEntryWithIndex(1, list.get(0));
		assertIsEntryWithIndex(2, list.get(1));

		try {
			list.get(2);

			Assert.fail("Could get removed entry.");
		} catch (IndexOutOfBoundsException e) {
			// expected
		}

		list = list.modify(createRemovingModification(1));

		assertIsEntryWithIndex(1, list.get(0));

		try {
			list.get(1);

			Assert.fail("Could get removed entry.");
		} catch (IndexOutOfBoundsException e) {
			// expected
		}
	}

	@Test
	public void testIterators() throws NoValueSetException,
			NoSuchEntryException, PolicyModificationException {
		list = list.modify(createAddingModification(0, 0));
		list = list.modify(createAddingModification(1, 1));
		list = list.modify(createAddingModification(2, 2));

		Iterator<W> it = list.iterator();

		Assert.assertTrue(it.hasNext());
		assertIsEntryWithIndex(0, it.next());

		Assert.assertTrue(it.hasNext());
		assertIsEntryWithIndex(1, it.next());

		Assert.assertTrue(it.hasNext());
		assertIsEntryWithIndex(2, it.next());

		Assert.assertFalse(it.hasNext());
	}

	protected abstract void assertIsEntryWithIndex(int i, W r);

	protected abstract void markEntryAsIndex(int i, W w);

	protected PolicyModification<EditableListInContent<W>> createAddingModification(
			final int index, final int markAsIndex) {
		return new PolicyModification<EditableListInContent<W>>() {

			@Override
			public void modify(EditableListInContent<W> newVersion)
					throws CMException {
				markEntryAsIndex(markAsIndex, newVersion.add(index));
			}
		};
	}

	protected PolicyModification<EditableListInContent<W>> createRemovingModification(
			final int index) {
		return new PolicyModification<EditableListInContent<W>>() {

			@Override
			public void modify(EditableListInContent<W> newVersion)
					throws CMException {
				newVersion.remove(index);
			}
		};
	}
}
