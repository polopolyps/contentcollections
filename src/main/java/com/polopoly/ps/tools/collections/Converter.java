package com.polopoly.ps.tools.collections;

import com.polopoly.ps.tools.collections.exception.ConversionException;

public interface Converter<T> {
	T fromString(String string) throws ConversionException;

	String toString(T value);
}
