package com.polopoly.ps.tools.collections.component;

import static com.polopoly.util.Require.require;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.ps.tools.collections.ComponentStorage;
import com.polopoly.ps.tools.collections.Converter;
import com.polopoly.ps.tools.collections.converter.ContentIdConverter;
import com.polopoly.ps.tools.collections.exception.ConversionException;
import com.polopoly.ps.tools.collections.exception.NoSuchComponentException;
import com.polopoly.util.collection.FetchingIterator;
import com.polopoly.util.content.ContentUtil;

public class DefaultComponentStorage<T> implements ComponentStorage<T> {
	private static final Logger LOGGER = Logger
			.getLogger(DefaultComponentStorage.class.getName());

	private String prefix;
	private Converter<T> converter;

	public DefaultComponentStorage(Converter<T> converter) {
		this("", converter);
	}

	public DefaultComponentStorage(String prefix, Converter<T> converter) {
		this.prefix = require(prefix);
		this.converter = require(converter);
	}

	@Override
	public void setComponent(ContentUtil content, String group,
			String component, T value) {
		String stringValue;

		if (value == null) {
			stringValue = null;
		} else {
			stringValue = converter.toString(value);
		}

		setNakedComponent(content, group, component, stringValue);
	}

	private void setNakedComponent(ContentUtil content, String group,
			String component, String stringValue) {
		content.setComponent(prefix + group, component, stringValue);
	}

	@Override
	public T getComponent(ContentUtil content, String group, String component)
			throws NoSuchComponentException {
		String result = getNakedComponent(content, group, component);

		try {
			return converter.fromString(result);
		} catch (ConversionException e) {
			LOGGER.log(
					Level.WARNING,
					"While getting component " + group + ":" + component
							+ " in " + this + " for " + content + ": "
							+ e.getMessage(), e);

			throw new NoSuchComponentException(e);
		}
	}

	private String getNakedComponent(ContentUtil content, String group,
			String component) throws NoSuchComponentException {
		String result = content.getComponent(prefix + group, component);

		if (result == null) {
			throw new NoSuchComponentException(group + ":" + component
					+ " did not exist in " + content);
		}

		return result;
	}

	public String toString() {
		if (prefix.equals("")) {
			return "default component storage";
		} else {
			return "component storage " + prefix;
		}
	}

	@Override
	public void clearComponent(ContentUtil content, String group,
			String component) {
		setComponent(content, group, component, null);
	}

	@Override
	public Iterator<String> groups(final ContentUtil content) {
		return new FetchingIterator<String>() {
			private String[] allGroups = content.getComponentGroupNames();
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
			private String[] componentArray = content.getComponentNames(prefix
					+ group);
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
