package com.polopoly.ps.tools.collections.componentcollection;

import com.polopoly.ps.tools.collections.Converter;
import com.polopoly.ps.tools.collections.component.DefaultComponentStorage;
import com.polopoly.ps.tools.collections.converter.BooleanConverter;
import com.polopoly.ps.tools.collections.incontent.ComponentCollectionProvider;
import com.polopoly.util.Require;
import com.polopoly.util.content.ContentUtil;

public class SetStorageProvider<T> extends
		AbstractProvider<SetStorage<T>, Boolean> implements
		ComponentCollectionProvider<SetStorage<T>> {
	private Converter<T> converter;

	public SetStorageProvider(Converter<T> converter) {
		this("", converter);
	}

	public SetStorageProvider(String prefix, Converter<T> converter) {
		super(new DefaultComponentStorage<Boolean>(prefix,
				new BooleanConverter()));

		this.converter = Require.require(converter);
	}

	@Override
	public SetStorage<T> getExisting(final String outerKey,
			final ContentUtil content) {
		return new MapStorageToSetStorageWrapper<T>(
				new MapStorageProvider.Storage<T, Boolean>(storage, converter,
						content, outerKey));
	}

}
