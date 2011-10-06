package com.polopoly.ps.tools.collections.incontent;

import java.util.Iterator;

import com.polopoly.ps.tools.collections.CollectionInContent;
import com.polopoly.ps.tools.collections.exception.NoSuchEntryException;
import com.polopoly.util.exception.PolicyModificationException;
import com.polopoly.util.policy.PolicyModification;

public interface MapInContent<W> extends Iterable<W>,
		CollectionInContent<EditableMapInContent<W>> {
	W get(String key) throws NoSuchEntryException;

	MapInContent<W> modify(
			PolicyModification<EditableMapInContent<W>> modification)
			throws PolicyModificationException;

	Iterator<String> keys();
}
