package com.polopoly.ps.tools.collections.examples;

import static com.polopoly.util.policy.Util.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.cm.policy.PolicyImplBase;
import com.polopoly.ps.tools.collections.component.DefaultComponentStorage;
import com.polopoly.ps.tools.collections.componentcollection.ListStorage;
import com.polopoly.ps.tools.collections.componentcollection.ListStorageProvider;
import com.polopoly.ps.tools.collections.converter.DateConverter;
import com.polopoly.ps.tools.collections.exception.NoSuchCollectionException;
import com.polopoly.util.content.ContentUtil;
import com.polopoly.util.exception.PolicyModificationException;
import com.polopoly.util.policy.PolicyModification;

/**
 * Illustrates how to store a list of values in a field inside a policy also
 * doing other things. In this case the field stores the dates of the last of
 * logins of a user in the user object.
 */
public class LastLoginField extends PolicyImplBase {
	private static final Logger LOGGER = Logger.getLogger(LastLoginField.class
			.getName());

	protected static final int NUMBER_OF_LOGINS_TO_KEEP = 16;

	private DefaultComponentStorage<Date> storage;

	private ListStorageProvider<Date> provider;

	@Override
	protected void initSelf() {
		super.initSelf();

		/*
		 * getPolicyName as prefix makes sure we are in our own name space among
		 * the components.
		 */
		storage = new DefaultComponentStorage<Date>(getPolicyName(),
				new DateConverter());

		provider = new ListStorageProvider<Date>(storage);

	}

	public Date getVeryLastLogin() {
		try {
			ListStorage<Date> list = provider.get("", util(this).getContent());

			return list.get(list.size() - 1);
		} catch (NoSuchCollectionException e) {
			return new Date();
		}
	}

	/**
	 * Returns oldest logins first.
	 */
	public List<Date> getLastLogins() {
		ListStorage<Date> list;

		try {
			list = provider.get("", util(this).getContent());
		} catch (NoSuchCollectionException e) {
			return Collections.emptyList();
		}

		List<Date> result = new ArrayList<Date>(list.size());

		for (Date date : list) {
			result.add(date);
		}

		return result;
	}

	public void loggedIn() {
		try {
			util(util(this).getTopPolicy()).modify(
					new PolicyModification<Policy>() {

						@Override
						public void modify(Policy newVersion)
								throws CMException {
							ListStorage<Date> list;
							ContentUtil content = util(newVersion).getContent();

							try {
								list = provider.get("", content);
							} catch (NoSuchCollectionException e) {
								list = provider.create("", content);
							}

							list.add(new Date());

							if (list.size() > NUMBER_OF_LOGINS_TO_KEEP) {
								// note that this can be a slow operation if the
								// list is large. Consider switching to
								// QueueStorageProvider if you only need
								// iterative
								// access anyway (i.e. if getVeryLastLogin is
								// not
								// relevant).
								list.remove(0);
							}
						}
					}, Policy.class);
		} catch (PolicyModificationException e) {
			LOGGER.log(Level.WARNING,
					"Could not store last login: " + e.getMessage(), e);
		}

	}
}
