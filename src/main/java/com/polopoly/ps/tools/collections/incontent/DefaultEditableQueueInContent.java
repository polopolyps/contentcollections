package com.polopoly.ps.tools.collections.incontent;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.ps.tools.collections.ComponentStorage;
import com.polopoly.util.policy.PolicyUtil;

public class DefaultEditableQueueInContent<W> extends
		DefaultQueueInContent<W> implements EditableQueueInContent<W> {
	private static final Logger LOGGER = Logger
			.getLogger(DefaultEditableListInContent.class.getName());

	public DefaultEditableQueueInContent(PolicyUtil policy,
			ComponentCollectionProvider<W> provider,
			ComponentStorage<?> storage, int maxSize) {
		super(policy, provider, storage, maxSize);
	}

	@Override
	public W push() {
		int oldEnd = getEnd();
		int newEnd = (oldEnd + 1) % maxSize;

		setEnd(newEnd);

		if (newEnd == getStart()) {
			setStart((getStart() + 1) % maxSize);
		}

		return provider.create(KEY_CONVERTER.toString(oldEnd), content);
	}

	@Override
	public void pop() {
		if (isEmpty()) {
			LOGGER.log(Level.WARNING, "Attempt to pop empty queue.");
			return;
		}

		int oldStart = getStart();
		int newStart = (oldStart + 1) % maxSize;

		setStart(newStart);
	}

	private void setEnd(int end) {
		positionStorage.setComponent(content, POSITION_GROUP, END_COMPONENT,
				end);
	}

	private void setStart(int start) {
		positionStorage.setComponent(content, POSITION_GROUP, START_COMPONENT,
				start);
	}

}
