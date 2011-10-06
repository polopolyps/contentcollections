package com.polopoly.ps.tools.collections.incontent;

import com.polopoly.ps.tools.collections.ComponentStorage;
import com.polopoly.ps.tools.collections.exception.NoSuchCollectionException;
import com.polopoly.util.policy.PolicyUtil;

public class DefaultEditableListInContent<W> extends DefaultListInContent<W>
		implements EditableListInContent<W> {

	public DefaultEditableListInContent(PolicyUtil policy,
			ComponentCollectionProvider<W> provider, ComponentStorage<?> storage) {
		super(policy, provider, storage);
	}

	@Override
	public W get(int index) throws IndexOutOfBoundsException {
		try {
			return provider.get(KEY_CONVERTER.toString(index), content);
		} catch (NoSuchCollectionException e) {
			throw new IndexOutOfBoundsException("Attempt to get index " + index
					+ " in list " + this + " of size " + size() + ".");
		}
	}

	@Override
	public W add() {
		return add(size());
	}

	protected void setSize(int newSize) {
		sizeStorage.setComponent(content, SIZE_GROUP, SIZE_COMPONENT, newSize);
	}

	@Override
	public W add(int index) {
		for (int i = size() - 1; i >= index; i--) {
			provider.move(KEY_CONVERTER.toString(i),
					KEY_CONVERTER.toString(i + 1), content);
		}

		setSize(size() + 1);

		return provider.create(KEY_CONVERTER.toString(index), content);
	}

	@Override
	public void remove(int index) {
		provider.clear(KEY_CONVERTER.toString(index), content);

		for (int i = index; i < size(); i++) {
			provider.move(KEY_CONVERTER.toString(i + 1),
					KEY_CONVERTER.toString(i), content);
		}

		setSize(size() - 1);
	}

}
