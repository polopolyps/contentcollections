package com.polopoly.ps.tools.collections.incontent;

import java.util.Iterator;

import com.polopoly.ps.tools.collections.exception.NoSuchCollectionException;
import com.polopoly.util.content.ContentUtil;

public interface ComponentCollectionProvider<W> {

	W get(String key, ContentUtil content) throws NoSuchCollectionException;

	W create(String key, ContentUtil content);

	void clear(String key, ContentUtil content);

	Iterator<W> values(ContentUtil content);

	Iterator<String> keys(ContentUtil content);

	void move(String key, String toKey, ContentUtil content);
}
