package com.polopoly.ps.tools.collections.converter;

import com.polopoly.ps.tools.collections.Converter;
import com.polopoly.ps.tools.collections.exception.ConversionException;

public class BooleanConverter implements Converter<Boolean> {

	@Override
	public Boolean fromString(String string) throws ConversionException {
		if (string == null) {
			throw new ConversionException();
		} else if (string.equals("true")) {
			return Boolean.TRUE;
		} else if (string.equals("false")) {
			return Boolean.FALSE;
		} else {
			throw new ConversionException("\"" + string
					+ "\" is not a boolean value.");
		}
	}

	@Override
	public String toString(Boolean booleanValue) {
		if (booleanValue) {
			return "true";
		} else {
			return "false";
		}
	}

}
