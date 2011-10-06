package com.polopoly.ps.tools.collections;

import java.util.Iterator;

import com.polopoly.ps.tools.collections.exception.NoSuchComponentException;
import com.polopoly.util.content.ContentUtil;

public interface ComponentStorage<T> {
	void clearComponent(ContentUtil content, String group, String component);

	void setComponent(ContentUtil content, String group, String component,
			T value);

	T getComponent(ContentUtil content, String group, String component)
			throws NoSuchComponentException;

	Iterator<String> groups(ContentUtil content);

	Iterator<String> components(ContentUtil content, String group);

	<O> ComponentStorage<O> getOtherwiseTypedStorage(Converter<O> converter);
}
