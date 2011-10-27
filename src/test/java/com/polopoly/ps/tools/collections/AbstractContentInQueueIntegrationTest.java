package com.polopoly.ps.tools.collections;

import static com.polopoly.util.policy.Util.util;
import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.polopoly.cm.client.CMException;
import com.polopoly.ps.test.AbstractIntegrationTest;
import com.polopoly.ps.test.client.ClientInitializer;
import com.polopoly.ps.test.client.NoSuchServiceException;
import com.polopoly.ps.test.client.PolopolyTestClientInitializer;
import com.polopoly.ps.tools.collections.incontent.EditableQueueInContent;
import com.polopoly.ps.tools.collections.incontent.QueueInContent;
import com.polopoly.util.policy.PolicyModification;
import com.polopoly.util.policy.PolicyUtil;

public abstract class AbstractContentInQueueIntegrationTest<W> extends AbstractIntegrationTest {
	private static final int BUFFER_SIZE = 10;
	protected QueueInContent<W> queue;
	protected PolicyUtil policy;

	protected int getMaxSize() {
		return BUFFER_SIZE - 1;
	}

	@Override
	protected ClientInitializer getInitializer() throws NoSuchServiceException {
		PolopolyTestClientInitializer result = new PolopolyTestClientInitializer();

		result.setAttachSolr(false);

		return result;
	}

	@Before
	public void setUp() throws Exception {
		policy = util(context.createPolicy(17, "p.DefaultAppConfig"));

		queue = createQueue();

		// this moves us forward one index which complicates things.
		queue = queue.modify(createPushingPoppingModification());
	}

	protected abstract QueueInContent<W> createQueue();

	@After
	public void tearDown() throws Exception {
		context.getPolicyCMServer().removeContent(policy.getContentId().unversioned());
	}

	@Test
	public void testPopEmpty() throws Exception {
		queue = queue.modify(new PolicyModification<EditableQueueInContent<W>>() {

			@Override
			public void modify(EditableQueueInContent<W> newVersion) throws CMException {
				newVersion.pop();
			}
		});

		Assert.assertEquals(0, queue.size());
	}

	@Test
	public void testPushUntilFull() throws Exception {
		queue = queue.modify(createPushingModification(0, getMaxSize() + 1));

		Assert.assertEquals(getMaxSize(), queue.size());
		assertIsEntryWithIndex(1, queue.iterator().next());
	}

	@Test
	public void testPushUntilMoreThanFull() throws Exception {
		queue = queue.modify(createPushingModification(0, getMaxSize() + 1));

		Assert.assertEquals(getMaxSize(), queue.size());
		assertIsEntryWithIndex(1, queue.iterator().next());
	}

	@Test
	public void testPushPop() throws Exception {
		queue = queue.modify(createPushingModification(0, 1));

		Assert.assertEquals(1, queue.size());
		assertIsEntryWithIndex(0, queue.iterator().next());

		queue = queue.modify(createPoppingModification());

		Assert.assertEquals(0, queue.size());
		Assert.assertFalse(queue.iterator().hasNext());
	}

	@Test
	public void testClear() throws Exception {
		queue = queue.modify(createPushingModification(0, 3));

		queue = queue.modify(new PolicyModification<EditableQueueInContent<W>>() {

			@Override
			public void modify(EditableQueueInContent<W> newVersion) throws CMException {
				newVersion.clear();
			}
		});

		Assert.assertEquals(0, queue.size());
		Assert.assertFalse(queue.iterator().hasNext());
	}

	protected PolicyModification<EditableQueueInContent<W>> createPoppingModification() {
		return new PolicyModification<EditableQueueInContent<W>>() {

			@Override
			public void modify(EditableQueueInContent<W> newVersion) throws CMException {
				newVersion.pop();
			}
		};
	}

	protected PolicyModification<EditableQueueInContent<W>> createPushingPoppingModification() {
		return new PolicyModification<EditableQueueInContent<W>>() {

			@Override
			public void modify(EditableQueueInContent<W> newVersion) throws CMException {
				newVersion.push();
				newVersion.pop();
			}
		};
	}

	protected PolicyModification<EditableQueueInContent<W>> createPushingModification(final int fromIndex,
			final int toIndex) {
		return new PolicyModification<EditableQueueInContent<W>>() {

			@Override
			public void modify(EditableQueueInContent<W> newVersion) throws CMException {
				for (int i = fromIndex; i < toIndex; i++) {
					markEntryAsIndex(i, newVersion.push());
				}
			}
		};
	}

	protected abstract void assertIsEntryWithIndex(int i, W r);

	protected abstract void markEntryAsIndex(int i, W w);
}
