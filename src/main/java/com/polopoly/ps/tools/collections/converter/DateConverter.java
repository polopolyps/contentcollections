package com.polopoly.ps.tools.collections.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.polopoly.ps.tools.collections.Converter;
import com.polopoly.ps.tools.collections.exception.ConversionException;

public class DateConverter implements Converter<Date> {
	private static final String FORMAT_STRING = "yyyyMMddHHmmss";
	private static final DateFormat FORMAT = new SimpleDateFormat(FORMAT_STRING);

	@Override
	public Date fromString(String string) throws ConversionException {
		if (string == null) {
			throw new ConversionException();
		}

		try {
			return FORMAT.parse(string);
		} catch (ParseException e) {
			throw new ConversionException();
		}
	}

	@Override
	public String toString(Date date) {
		return FORMAT.format(date);
	}

}
