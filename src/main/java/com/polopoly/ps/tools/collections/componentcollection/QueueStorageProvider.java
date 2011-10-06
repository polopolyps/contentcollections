package com.polopoly.ps.tools.collections.componentcollection;

import com.polopoly.ps.tools.collections.ComponentStorage;
import com.polopoly.ps.tools.collections.converter.IntegerConverter;
import com.polopoly.ps.tools.collections.incontent.ComponentCollectionProvider;
import com.polopoly.util.content.ContentUtil;

public class QueueStorageProvider<T> extends
		AbstractProvider<QueueStorage<T>, T> implements
		ComponentCollectionProvider<QueueStorage<T>> {

	private int maxSize;

	public QueueStorageProvider(ComponentStorage<T> storage, int maxSize) {
		super(storage);

		this.maxSize = maxSize;
	}

	@Override
	public QueueStorage<T> getExisting(final String outerKey,
			final ContentUtil content) {
		return new MapStorageToQueueStorageWrapper<T>(
				new MapStorageProvider.Storage<Integer, T>(storage,
						new IntegerConverter(), content, outerKey),
				getStorage(), content, outerKey, maxSize);
	}

}
