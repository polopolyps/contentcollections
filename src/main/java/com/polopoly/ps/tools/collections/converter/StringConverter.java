package com.polopoly.ps.tools.collections.converter;

import com.polopoly.ps.tools.collections.Converter;
import com.polopoly.ps.tools.collections.exception.ConversionException;

public class StringConverter implements Converter<String> {

	@Override
	public String fromString(String string) throws ConversionException {
		if (string == null) {
			throw new ConversionException();
		}

		return string;
	}

	@Override
	public String toString(String string) {
		return string;
	}

}
