package com.polopoly.ps.tools.collections;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import com.polopoly.cm.client.CMException;
import com.polopoly.ps.tools.collections.component.DefaultComponentStorage;
import com.polopoly.ps.tools.collections.componentcollection.QueueStorage;
import com.polopoly.ps.tools.collections.componentcollection.QueueStorageProvider;
import com.polopoly.ps.tools.collections.converter.IntegerConverter;
import com.polopoly.ps.tools.collections.incontent.DefaultQueueInContent;
import com.polopoly.ps.tools.collections.incontent.EditableQueueInContent;
import com.polopoly.ps.tools.collections.incontent.QueueInContent;
import com.polopoly.util.policy.PolicyModification;

public class QueueInQueueIntegrationTest extends AbstractContentInQueueIntegrationTest<QueueStorage<Integer>> {

	@Override
	protected QueueInContent<QueueStorage<Integer>> createQueue() {
		QueueStorageProvider<Integer> componentCollectionProvider = new QueueStorageProvider<Integer>(
				new DefaultComponentStorage<Integer>(new IntegerConverter()), getMaxSize());

		return new DefaultQueueInContent<QueueStorage<Integer>>(policy, componentCollectionProvider,
				componentCollectionProvider.getStorage(), getMaxSize() + 1);
	}

	@Override
	protected void assertIsEntryWithIndex(int i, QueueStorage<Integer> r) {
		if (!r.iterator().next().equals(i)) {
			Assert.fail("Expected the queue to contain " + i + "; was " + r);
		}
	}

	@Override
	protected void markEntryAsIndex(int i, QueueStorage<Integer> w) {
		w.push(new Integer(i));
	}

	@Test
	public void testAddRemove() throws Exception {
		queue = queue.modify(new PolicyModification<EditableQueueInContent<QueueStorage<Integer>>>() {

			@Override
			public void modify(EditableQueueInContent<QueueStorage<Integer>> newVersion) throws CMException {
				QueueStorage<Integer> queue = newVersion.push();

				queue.push(0);
				queue.pop();

				queue.push(1);
				queue.push(2);

				Assert.assertEquals(2, queue.size());

				Assert.assertEquals(new Integer(1), queue.pop());

				Assert.assertEquals(1, queue.size());
			}
		});
	}

	@Test
	public void testIterator() throws Exception {
		queue = queue.modify(new PolicyModification<EditableQueueInContent<QueueStorage<Integer>>>() {

			@Override
			public void modify(EditableQueueInContent<QueueStorage<Integer>> newVersion) throws CMException {
				QueueStorage<Integer> queue = newVersion.push();

				queue.push(0);
				queue.pop();

				for (int i = 0; i <= getMaxSize(); i++) {
					queue.push(i + 1);
				}

				Assert.assertEquals(getMaxSize(), queue.size());

				Iterator<Integer> it = queue.iterator();

				for (int i = 0; i < getMaxSize(); i++) {
					Assert.assertTrue(it.hasNext());
					Assert.assertEquals((Integer) (i + 2), it.next());
				}

			}
		});
	}
}
