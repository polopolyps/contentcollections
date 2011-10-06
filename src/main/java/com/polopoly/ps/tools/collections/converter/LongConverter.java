package com.polopoly.ps.tools.collections.converter;

import com.polopoly.ps.tools.collections.Converter;
import com.polopoly.ps.tools.collections.exception.ConversionException;

public class LongConverter implements Converter<Long> {

	@Override
	public Long fromString(String string) throws ConversionException {
		if (string == null) {
			throw new ConversionException();
		}

		try {
			return Long.parseLong(string);
		} catch (NumberFormatException e) {
			throw new ConversionException();
		}
	}

	@Override
	public String toString(Long longValue) {
		return Long.toString(longValue);
	}

}
