package org.rr.expander.feed;

import javax.annotation.Nonnull;

/**
 * Factory interface which can be used with guice to create a {@link FeedBuilder} implementation.
 */
public interface FeedBuilderFactory {
	
	public FeedBuilder createFeedBuilder(@Nonnull String feedUrl);
	
}
