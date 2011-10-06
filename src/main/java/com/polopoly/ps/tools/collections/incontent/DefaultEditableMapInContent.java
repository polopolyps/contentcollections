package com.polopoly.ps.tools.collections.incontent;

import com.polopoly.ps.tools.collections.exception.NoSuchCollectionException;
import com.polopoly.ps.tools.collections.exception.NoSuchEntryException;
import com.polopoly.util.policy.PolicyUtil;

public class DefaultEditableMapInContent<W> extends
		DefaultMapInContent<W> implements EditableMapInContent<W> {

	public DefaultEditableMapInContent(PolicyUtil policy,
			ComponentCollectionProvider<W> provider) {
		super(policy, provider);
	}

	@Override
	public W get(String key) throws NoSuchEntryException {
		try {
			return provider.get(key, content);
		} catch (NoSuchCollectionException e) {
			throw new NoSuchEntryException(e);
		}
	}

	@Override
	public W create(String key) {
		return provider.create(key, content);
	}

	@Override
	public void clear(String key) {
		provider.clear(key, content);
	}

}
