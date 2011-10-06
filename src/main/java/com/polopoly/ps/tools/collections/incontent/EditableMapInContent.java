package com.polopoly.ps.tools.collections.incontent;

import com.polopoly.ps.tools.collections.EditableCollectionInContent;
import com.polopoly.ps.tools.collections.exception.NoSuchEntryException;

public interface EditableMapInContent<W> extends
		MapInContent<W>, EditableCollectionInContent {
	W get(String key) throws NoSuchEntryException;

	void clear(String key);

	W create(String key);
}
