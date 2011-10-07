package com.polopoly.ps.tools.collections.incontent;

public interface EditableQueueInContent<W> extends QueueInContent<W> {
	W push();

	void pop();

	void clear();
}
