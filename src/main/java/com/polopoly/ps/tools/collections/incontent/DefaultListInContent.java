package com.polopoly.ps.tools.collections.incontent;

import static com.polopoly.util.Require.require;
import static com.polopoly.util.policy.Util.util;

import java.util.Iterator;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.ps.tools.collections.ComponentStorage;
import com.polopoly.ps.tools.collections.Converter;
import com.polopoly.ps.tools.collections.converter.IntegerConverter;
import com.polopoly.ps.tools.collections.exception.NoSuchCollectionException;
import com.polopoly.ps.tools.collections.exception.NoSuchComponentException;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.exception.PolicyModificationException;
import com.polopoly.util.policy.PolicyModification;
import com.polopoly.util.policy.PolicyUtil;

public class DefaultListInContent<W> implements ListInContent<W> {
	protected static final Converter<Integer> KEY_CONVERTER = new IntegerConverter();
	protected static final Converter<Integer> SIZE_CONVERTER = new IntegerConverter();

	protected static final String SIZE_GROUP = "size";
	protected static final String SIZE_COMPONENT = "value";

	protected ComponentCollectionProvider<W> provider;
	protected PolicyUtil policy;
	protected ContentUtil content;

	protected ComponentStorage<Integer> sizeStorage;

	public DefaultListInContent(PolicyUtil policy,
			ComponentCollectionProvider<W> provider, ComponentStorage<?> storage) {
		this.provider = require(provider);
		this.policy = require(policy);
		this.sizeStorage = require(storage).getOtherwiseTypedStorage(
				new IntegerConverter());
		this.content = policy.getContent();
	}

	@Override
	public DefaultListInContent<W> modify(
			final PolicyModification<EditableListInContent<W>> modification)
			throws PolicyModificationException {
		Policy result = policy.modify(new PolicyModification<Policy>() {
			@Override
			public void modify(Policy newVersion) throws CMException {
				modification.modify(new DefaultEditableListInContent<W>(
						util(newVersion), provider, sizeStorage));
			}
		}, Policy.class);

		return new DefaultListInContent<W>(util(result), provider, sizeStorage);
	}

	@Override
	public W get(int index) throws IndexOutOfBoundsException {
		try {
			return provider.get(KEY_CONVERTER.toString(index), content);
		} catch (NoSuchCollectionException e) {
			throw new IndexOutOfBoundsException();
		}
	}

	@Override
	public Iterator<W> iterator() {
		return new Iterator<W>() {
			int i = 0;
			int size = size();

			@Override
			public boolean hasNext() {
				return i < size;
			}

			@Override
			public W next() {
				if (!hasNext()) {
					throw new IllegalStateException();
				}

				return get(i++);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public int size() {
		try {
			return sizeStorage
					.getComponent(content, SIZE_GROUP, SIZE_COMPONENT);
		} catch (NoSuchComponentException e) {
			return 0;
		}
	}

	public String toString() {
		return "list in " + policy + " (collection provider: " + provider
				+ ", size: " + size() + ")";
	}
}
