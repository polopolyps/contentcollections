package com.polopoly.ps.tools.collections.converter;

import static com.polopoly.util.Require.require;
import static com.polopoly.util.policy.Util.util;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.ContentIdFactory;
import com.polopoly.ps.tools.collections.Converter;
import com.polopoly.ps.tools.collections.exception.ConversionException;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.contentid.ContentIdUtil;

public class ContentIdConverter implements Converter<ContentId> {
	private PolopolyContext context;

	public ContentIdConverter(PolopolyContext context) {
		this.context = require(context);
	}

	@Override
	public ContentIdUtil fromString(String string) throws ConversionException {
		if (string == null) {
			throw new ConversionException();
		}

		try {
			return util(ContentIdFactory.createContentId(string), context);
		} catch (IllegalArgumentException e) {
			throw new ConversionException();
		}
	}

	@Override
	public String toString(ContentId value) {
		return value.getContentId().getContentIdString();
	}

}
