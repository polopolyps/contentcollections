package com.polopoly.ps.tools.collections.examples;

import java.util.Date;

import com.polopoly.cm.ContentId;

public interface PersistedLogEntry {
	String getEvent();

	ContentId getContentId();

	Date getDate();
}
