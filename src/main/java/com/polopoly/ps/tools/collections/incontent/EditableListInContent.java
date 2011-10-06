package com.polopoly.ps.tools.collections.incontent;

import com.polopoly.ps.tools.collections.EditableCollectionInContent;

public interface EditableListInContent<W> extends ListInContent<W>,
		EditableCollectionInContent {
	W get(int index) throws IndexOutOfBoundsException;

	W add();

	W add(int index);

	void remove(int index);
}
