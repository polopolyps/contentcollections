package com.polopoly.ps.tools.collections.converter;

import com.polopoly.ps.tools.collections.Converter;
import com.polopoly.ps.tools.collections.exception.ConversionException;

public class IntegerConverter implements Converter<Integer> {

	@Override
	public Integer fromString(String string) throws ConversionException {
		if (string == null) {
			throw new ConversionException("Asked to convert null value.");
		}

		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			throw new ConversionException(string + " was not a number.");
		}
	}

	@Override
	public String toString(Integer longValue) {
		return Integer.toString(longValue);
	}

}
