package org.rr.expander;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.rr.expander.feed.FeedBuilder;
import org.rr.expander.feed.FeedBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.sun.syndication.io.FeedException;

@Path("/expand")
public class ExpanderResource {
	
	@Nonnull
	private final static Logger logger = LoggerFactory.getLogger(ExpanderResource.class);

	@Nonnull
	@Inject(optional = false)
	private ExpanderFeedSitesManager feedSitesManager;
	
	@Nonnull
	@Inject(optional = false)
	private FeedBuilderFactory feedBuilderFactory;
	
	@PermitAll
	@GET
	public Response expand(@QueryParam("alias") Optional<String> alias) {
		if(alias.isPresent()) {
			return expandByAlias(alias);
		}
		return getBadRequestResponse();
	}
	
	@Nonnull
	private Response expandByAlias(@Nonnull Optional<String> alias) {
		return alias.transform(new Function<String, Response>() {
			@Override
			public Response apply(@Nullable String alias) {
				try {
					if(alias != null && feedSitesManager.containsAlias(alias)) {
						FeedBuilder feedHandler = createFeedHandlerForAlias(alias);
						return getSuccessResponse(feedHandler);
					} 
					logger.warn(String.format("Fetching feed for alias '%s' is not allowed.", alias));
					return getForbiddenResponse();
				} catch (Exception e) {
					logger.warn(String.format("Fetching feed for alias '%s' has failed.", alias), e);
					return getInternalServerErrorResponse();
				}
			}
		}).or(getBadRequestResponse()); // (no alias)
	}

	@Nonnull
	private FeedBuilder createFeedHandlerForAlias(@Nonnull String alias)
			throws MalformedURLException, FeedException, IOException {
		FeedBuilder feedHandler = feedBuilderFactory.createFeedBuilder(feedSitesManager.getFeedUrl(alias))
				.loadFeed()
				.applyLimit(feedSitesManager.getLimit(alias))
				.filter(feedSitesManager.getIncludeFilter(alias), feedSitesManager.getExcludeFilter(alias))
				.expand(feedSitesManager.getSelector(alias))
				.filter(feedSitesManager.getIncludeFilter(alias), feedSitesManager.getExcludeFilter(alias));
		return feedHandler;
	}
	
	@Nonnull
	private Response getSuccessResponse(FeedBuilder feedBuilder) throws FeedException {
		return Response.ok(feedBuilder.build(), feedBuilder.getMediaType())
				.header("X-Robots-Tag", "noindex, nofollow")
				.build();
	}

	@Nonnull
	private Response getInternalServerErrorResponse() {
		return getResponse(500);
	}

	@Nonnull
	private Response getForbiddenResponse() {
		return getResponse(403);
	}
	
	@Nonnull
	private Response getBadRequestResponse() {
		return getResponse(400);
	}
	
	@Nonnull
	private Response getResponse(int state) {
		return Response.status(state).build();
	}	
}
