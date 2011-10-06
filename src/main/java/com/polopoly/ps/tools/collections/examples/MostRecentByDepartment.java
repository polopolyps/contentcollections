package com.polopoly.ps.tools.collections.examples;

import static com.polopoly.util.policy.Util.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.polopoly.cm.ContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.Policy;
import com.polopoly.ps.tools.collections.ComponentStorage;
import com.polopoly.ps.tools.collections.component.ReferenceComponentStorage;
import com.polopoly.ps.tools.collections.componentcollection.QueueStorage;
import com.polopoly.ps.tools.collections.componentcollection.QueueStorageProvider;
import com.polopoly.ps.tools.collections.converter.ContentIdConverter;
import com.polopoly.ps.tools.collections.exception.NoSuchEntryException;
import com.polopoly.ps.tools.collections.incontent.DefaultMapInContent;
import com.polopoly.ps.tools.collections.incontent.EditableMapInContent;
import com.polopoly.ps.tools.collections.incontent.MapInContent;
import com.polopoly.util.Require;
import com.polopoly.util.client.PolopolyContext;
import com.polopoly.util.collection.FetchingIterator;
import com.polopoly.util.contentid.ContentIdUtil;
import com.polopoly.util.exception.CannotFetchSingletonException;
import com.polopoly.util.exception.PolicyGetException;
import com.polopoly.util.exception.PolicyModificationException;
import com.polopoly.util.policy.PolicyModification;
import com.polopoly.util.policy.PolicySingleton;
import com.polopoly.util.policy.PolicyUtil;

/**
 * An example illustrating how to store a list of newest objects by department
 * persistently. The list is notified when new objects are created so retrieving
 * the list just involves a lookup in the content object rather than a search.
 * 
 * Note that this code would not be suitable for a large number of departments
 * and large queues since the total number of entries in a content object
 * shouldn't exceed a thousand or so.
 */
public class MostRecentByDepartment {
	private static final Logger LOGGER = Logger
			.getLogger(MostRecentByDepartment.class.getName());

	private PolicyUtil policy;

	private MapInContent<QueueStorage<ContentIdUtil>> map;

	private static final ComponentStorage<ContentIdUtil> storage = new ReferenceComponentStorage();

	private static final int MAX_OBJECT_COUNT = 100;

	private static final QueueStorageProvider<ContentIdUtil> provider = new QueueStorageProvider<ContentIdUtil>(
			storage, MAX_OBJECT_COUNT);

	private static final String EXTERNAL_ID = "mostRecentByDepartment";

	private ContentIdConverter keyConverter;

	private MostRecentByDepartment(PolicyUtil policy) {
		this.policy = Require.require(policy);

		map = new DefaultMapInContent<QueueStorage<ContentIdUtil>>(policy,
				provider);

		keyConverter = new ContentIdConverter(policy.getContext());
	}

	public static MostRecentByDepartment getMostRecentByDepartment(
			PolopolyContext context) throws CannotFetchSingletonException {
		Policy policy = new PolicySingleton(context, 17, EXTERNAL_ID,
				"p.DefaultAppConfig").get();

		return new MostRecentByDepartment(util(policy));
	}

	/**
	 * The specified object belonging to the specified department has been
	 * created.
	 */
	public void postCreateNotification(final Policy policy,
			final ContentId department) {
		try {
			map.modify(new PolicyModification<EditableMapInContent<QueueStorage<ContentIdUtil>>>() {

				@Override
				public void modify(
						EditableMapInContent<QueueStorage<ContentIdUtil>> newVersion)
						throws CMException {

					String key = keyConverter.toString(department);

					QueueStorage<ContentIdUtil> queue;
					try {
						queue = newVersion.get(key);
					} catch (NoSuchEntryException e) {
						queue = newVersion.create(key);
					}

					queue.push(util(policy).getContentId());
				}
			});
		} catch (PolicyModificationException e) {
			LOGGER.log(Level.WARNING,
					"Could not update list of newest objects for department "
							+ department.getContentIdString() + " with "
							+ util(policy));
		}
	}

	public <T> Iterator<T> getMostRecent(final ContentId department,
			final Class<T> policyClass) {
		try {
			final QueueStorage<ContentIdUtil> queue = map
					.get(new ContentIdConverter(policy.getContext())
							.toString(department));

			return new FetchingIterator<T>() {
				Iterator<ContentIdUtil> delegate = queue.iterator();

				@Override
				protected T fetch() {
					if (!delegate.hasNext()) {
						return null;
					}

					try {
						return delegate.next().asPolicy(policyClass);
					} catch (PolicyGetException e) {
						LOGGER.log(Level.WARNING,
								"Could not fetch object stored as most recent in department "
										+ department.getContentIdString()
										+ ": " + e.getMessage(), e);

						return fetch();
					}
				}
			};
		} catch (NoSuchEntryException e) {
			Set<T> emptySet = Collections.emptySet();
			return emptySet.iterator();
		}
	}
}
