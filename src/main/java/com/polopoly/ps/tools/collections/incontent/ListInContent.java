package com.polopoly.ps.tools.collections.incontent;

import com.polopoly.ps.tools.collections.CollectionInContent;
import com.polopoly.util.exception.PolicyModificationException;
import com.polopoly.util.policy.PolicyModification;

public interface ListInContent<W> extends Iterable<W>,
		CollectionInContent<EditableListInContent<W>> {
	W get(int index) throws IndexOutOfBoundsException;

	int size();

	ListInContent<W> modify(
			PolicyModification<EditableListInContent<W>> modification)
			throws PolicyModificationException;
}
