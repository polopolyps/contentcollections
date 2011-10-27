package com.polopoly.ps.tools.collections.incontent;

import static com.polopoly.util.Require.require;
import static com.polopoly.util.policy.Util.util;

import java.util.Iterator;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.ps.tools.collections.ComponentStorage;
import com.polopoly.ps.tools.collections.Converter;
import com.polopoly.ps.tools.collections.converter.IntegerConverter;
import com.polopoly.ps.tools.collections.exception.NoSuchCollectionException;
import com.polopoly.ps.tools.collections.exception.NoSuchComponentException;
import com.polopoly.ps.tools.collections.exception.QueueIsEmptyException;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.exception.PolicyModificationException;
import com.polopoly.util.policy.PolicyModification;
import com.polopoly.util.policy.PolicyUtil;

public class DefaultQueueInContent<W> implements QueueInContent<W> {
	protected static final Converter<Integer> KEY_CONVERTER = new IntegerConverter();
	protected static final Converter<Integer> POSITION_CONVERTER = new IntegerConverter();

	protected static final String POSITION_GROUP = "position";
	protected static final String START_COMPONENT = "start";
	protected static final String END_COMPONENT = "end";

	protected ComponentCollectionProvider<W> provider;
	protected PolicyUtil policy;
	protected ContentUtil content;

	protected ComponentStorage<Integer> positionStorage;

	protected int bufferSize;

	public DefaultQueueInContent(PolicyUtil policy, ComponentCollectionProvider<W> provider,
			ComponentStorage<?> storage, int bufferSize) {
		this.provider = require(provider);
		this.policy = require(policy);
		this.positionStorage = storage.getOtherwiseTypedStorage(POSITION_CONVERTER);
		this.content = policy.getContent();
		this.bufferSize = bufferSize;
	}

	@Override
	public DefaultQueueInContent<W> modify(final PolicyModification<EditableQueueInContent<W>> modification)
			throws PolicyModificationException {
		Policy result = policy.modify(new PolicyModification<Policy>() {
			@Override
			public void modify(Policy newVersion) throws CMException {
				modification.modify(new DefaultEditableQueueInContent<W>(util(newVersion), provider,
						positionStorage, bufferSize));
			}
		}, Policy.class);

		return new DefaultQueueInContent<W>(util(result), provider, positionStorage, bufferSize);
	}

	private W get(int index) throws IndexOutOfBoundsException {
		try {
			return provider.get(KEY_CONVERTER.toString(index), content);
		} catch (NoSuchCollectionException e) {
			throw new IndexOutOfBoundsException();
		}
	}

	@Override
	public Iterator<W> iterator() {
		return new Iterator<W>() {
			int at = getStart();
			int end = getEnd();

			@Override
			public boolean hasNext() {
				return at != end;
			}

			@Override
			public W next() {
				if (!hasNext()) {
					throw new IllegalStateException();
				}

				try {
					return get(at);
				} finally {
					at = (at + 1) % bufferSize;
				}
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public int size() {
		int start = getStart();
		int end = getEnd();

		if (end >= start) {
			return end - start;
		} else {
			return bufferSize - start + end;
		}
	}

	protected int getEnd() {
		try {
			return positionStorage.getComponent(content, POSITION_GROUP, END_COMPONENT);
		} catch (NoSuchComponentException e) {
			return 0;
		}
	}

	protected int getStart() {
		try {
			return positionStorage.getComponent(content, POSITION_GROUP, START_COMPONENT);
		} catch (NoSuchComponentException e) {
			return 0;
		}
	}

	public String toString() {
		return "list in " + policy + " (collection provider: " + provider + ")";
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public W getFirst() throws QueueIsEmptyException {
		if (isEmpty()) {
			throw new QueueIsEmptyException();
		}

		return get(getStart());
	}
}
