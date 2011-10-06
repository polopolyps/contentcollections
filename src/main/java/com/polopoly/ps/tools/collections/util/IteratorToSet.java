package com.polopoly.ps.tools.collections.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class IteratorToSet {
	public static <T> Set<T> toSet(Iterator<T> iterator) {
		Set<T> result = new HashSet<T>();

		while (iterator.hasNext()) {
			result.add(iterator.next());
		}

		return result;
	}

}
