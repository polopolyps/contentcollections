package com.polopoly.ps.tools.collections.component;

import static com.polopoly.util.Require.require;

import java.util.Iterator;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CMRuntimeException;
import com.polopoly.ps.tools.collections.ComponentStorage;
import com.polopoly.ps.tools.collections.Converter;
import com.polopoly.ps.tools.collections.converter.ContentIdConverter;
import com.polopoly.ps.tools.collections.exception.NoSuchComponentException;
import com.polopoly.util.collection.FetchingIterator;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.contentid.ContentIdUtil;

public class ReferenceComponentStorage implements
		ComponentStorage<ContentIdUtil> {

	private String prefix;

	public ReferenceComponentStorage() {
		this("");
	}

	public ReferenceComponentStorage(String prefix) {
		this.prefix = require(prefix);
	}

	@Override
	public void clearComponent(ContentUtil content, String group,
			String component) {
		try {
			content.setContentReference(prefix + group, component, null);
		} catch (CMException e) {
			// this can't happen. should be moved to contentutil.
			throw new CMRuntimeException("While setting component: " + e, e);
		}
	}

	@Override
	public void setComponent(ContentUtil content, String group,
			String component, ContentIdUtil value) {
		try {
			content.setContentReference(prefix + group, component, value);
		} catch (CMException e) {
			// this can't happen. should be moved to contentutil.
			throw new CMRuntimeException("While setting component: " + e, e);
		}
	}

	@Override
	public ContentIdUtil getComponent(ContentUtil content, String group,
			String component) throws NoSuchComponentException {
		return content.getContentReference(prefix + group, component);
	}

	@Override
	public Iterator<String> groups(final ContentUtil content) {
		return new FetchingIterator<String>() {
			private String[] allGroups = content
					.getContentReferenceGroupNames();
			private int at = 0;

			@Override
			protected String fetch() {
				while (at < allGroups.length) {
					try {
						if (allGroups[at].startsWith(prefix)) {
							return allGroups[at];
						}
					} finally {
						at++;
					}
				}

				return null;
			}
		};
	}

	@Override
	public Iterator<String> components(final ContentUtil content,
			final String group) {
		return new FetchingIterator<String>() {
			private String[] componentArray = content
					.getContentReferenceNames(prefix + group);
			private int at = 0;

			@Override
			protected String fetch() {
				if (at < componentArray.length) {
					return componentArray[at++];
				}

				return null;
			}
		};

	}

	@SuppressWarnings("unchecked")
	@Override
	public <O> ComponentStorage<O> getOtherwiseTypedStorage(
			Converter<O> converter) {
		if (converter instanceof ContentIdConverter) {
			return (ComponentStorage<O>) new ReferenceComponentStorage(prefix);
		} else {
			return new DefaultComponentStorage<O>(prefix, converter);
		}
	}

}
