package com.polopoly.ps.tools.collections.componentcollection;

import com.polopoly.ps.tools.collections.ComponentStorage;
import com.polopoly.ps.tools.collections.converter.IntegerConverter;
import com.polopoly.ps.tools.collections.incontent.ComponentCollectionProvider;
import com.polopoly.util.content.ContentUtil;

public class ListStorageProvider<T> extends
		AbstractProvider<ListStorage<T>, T> implements
		ComponentCollectionProvider<ListStorage<T>> {

	public ListStorageProvider(ComponentStorage<T> storage) {
		super(storage);
	}

	@Override
	public ListStorage<T> getExisting(final String outerKey,
			final ContentUtil content) {
		return new MapStorageToListStorageWrapper<T>(
				new MapStorageProvider.Storage<Integer, T>(storage,
						new IntegerConverter(), content, outerKey),
				getStorage(), content, outerKey);
	}

}
