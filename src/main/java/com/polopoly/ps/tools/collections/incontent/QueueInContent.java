package com.polopoly.ps.tools.collections.incontent;

import com.polopoly.ps.tools.collections.CollectionInContent;
import com.polopoly.ps.tools.collections.exception.QueueIsEmptyException;
import com.polopoly.util.exception.PolicyModificationException;
import com.polopoly.util.policy.PolicyModification;

public interface QueueInContent<W> extends Iterable<W>,
		CollectionInContent<EditableListInContent<W>> {
	int size();

	boolean isEmpty();

	W getFirst() throws QueueIsEmptyException;

	QueueInContent<W> modify(
			PolicyModification<EditableQueueInContent<W>> modification)
			throws PolicyModificationException;
}
