package com.polopoly.ps.tools.collections.examples;

import java.util.Date;
import java.util.logging.Level;

import com.polopoly.cm.ContentId;
import com.polopoly.ps.tools.collections.componentcollection.MapStorage;
import com.polopoly.ps.tools.collections.converter.ContentIdConverter;
import com.polopoly.ps.tools.collections.converter.DateConverter;
import com.polopoly.ps.tools.collections.exception.ConversionException;
import com.polopoly.ps.tools.collections.exception.NoSuchEntryException;
import com.polopoly.util.client.PolopolyContext;

class WrappingLogEntry implements PersistedLogEntry {
	private final MapStorage<String, String> next;
	private PolopolyContext context;

	WrappingLogEntry(PolopolyContext context, MapStorage<String, String> next) {
		this.next = next;
		this.context = context;
	}

	@Override
	public String getEvent() {
		try {
			return next.get(PersistedLog.EVENT);
		} catch (NoSuchEntryException e) {
			exceptionFetching(e);

			return "";
		}
	}

	@Override
	public ContentId getContentId() {
		try {
			return new ContentIdConverter(context).fromString(next
					.get(PersistedLog.CONTENT_ID));
		} catch (ConversionException e) {
			exceptionFetching(e);

			return new ContentId(2, 1);
		} catch (NoSuchEntryException e) {
			exceptionFetching(e);

			return new ContentId(2, 1);
		}
	}

	@Override
	public Date getDate() {
		try {
			return new DateConverter().fromString(next.get(PersistedLog.DATE));
		} catch (ConversionException e) {
			exceptionFetching(e);
			return new Date();
		} catch (NoSuchEntryException e) {
			exceptionFetching(e);
			return new Date();
		}
	}

	private void exceptionFetching(Exception e) {
		PersistedLog.LOGGER.log(Level.WARNING, "While parsing log entry "
				+ next + ": " + e.getMessage(), e);
	}
}