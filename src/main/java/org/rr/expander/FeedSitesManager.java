package org.rr.expander;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;

/**
 * Manager which is able to parse and provide values from the configuration file specified with the constructor. 
 */
public class FeedSitesManager {
	
	private static class Entries {
		
    @JsonProperty("feeds")
    private List<Entry> entries;

    public List<Entry> getEntries() {
        return entries;
    }

	}
	
	private static class Entry {
		
		@JsonProperty("alias")
		private String alias;
		@JsonProperty("description")
		private String description;
		@JsonProperty("feedUrl")
		private String feedUrl;
		@JsonProperty("selector")
		private String selector;
		@JsonProperty("limit")
		private int limit;
		@JsonProperty("includeFilter")
		private String includeFilter;
		@JsonProperty("excludeFilter")
		private String excludeFilter;
		
		
		public String getDescription() {
			return description;
		}

		public String getFeedUrl() {
			return feedUrl;
		}

		public String getSelector() {
			return selector;
		}

		public int getLimit() {
			return limit;
		}

		public String getAlias() {
			return alias;
		}

		public String getIncludeFilter() {
			return includeFilter;
		}

		public String getExcludeFilter() {
			return excludeFilter;
		}
	}
	
	@Nonnull
	private final static Logger logger = LoggerFactory.getLogger(FeedSitesManager.class);
	
	@Nonnull
	private static final String SEPARATOR_CHAR = "|";

	/** the feed sites config file name. */
	private Path feedSitesFile;
	
	/** stores the modified time stamp of the time when the config file was read the last time. */
	private long feedSitesFileModified;
	
	/** the feed site configuration will be stored here. */
	private Map<String, Entry> feedSiteEntries;

	public FeedSitesManager(@Nullable String feedSitesFile) {
		this.feedSitesFile = Paths.get(feedSitesFile);
	}
	
	public boolean containsAlias(@Nullable String alias) throws IOException {
		if(isNotBlank(alias)) {
			return getEntries().containsKey(alias);
		}
		return false;
	}
	
	public int size() throws IOException {
		return getEntries().size();
	}
	
	@Nullable
	public String getDescription(@Nullable String alias) throws IOException {
		return Optional.ofNullable(getEntries().get(alias)).orElse(new Entry()).getDescription();
	}
	
	@Nullable
	public String getFeedUrl(@Nullable String alias) throws IOException {
		return Optional.ofNullable(getEntries().get(alias)).orElse(new Entry()).getFeedUrl();
	}

	@Nullable
	public String getSelector(@Nullable String alias) throws IOException {
		return Optional.ofNullable(getEntries().get(alias)).orElse(new Entry()).getSelector();
	}
	
	@Nullable
	public String getIncludeFilter(@Nullable String alias) throws IOException {
		return Optional.ofNullable(getEntries().get(alias)).orElse(new Entry()).getIncludeFilter();
	}
	
	@Nullable
	public String getExcludeFilter(@Nullable String alias) throws IOException {
		return Optional.ofNullable(getEntries().get(alias)).orElse(new Entry()).getExcludeFilter();
	}
	
	@Nullable
	public Integer getLimit(@Nullable String alias) throws IOException {
		return Optional.ofNullable(getEntries().get(alias)).orElse(new Entry()).getLimit();
	}	
	
	@Nonnull
	public Set<String> getAliases() throws IOException {
		return getEntries().keySet();
	}

	@Nonnull
	private Map<String, Entry>  getEntries() throws IOException {
		if(feedSiteEntries == null || isReReadFeedSitesFileNecessary()) {
			feedSiteEntries = readFeedSitesFile();
		}
		return feedSiteEntries;
	}
	
	private boolean isReReadFeedSitesFileNecessary() {
		return feedSitesFileModified == 0 ||  
				feedSitesFileModified < feedSitesFile.toFile().lastModified();
	}
	
	@Nonnull
	private Map<String, Entry> readFeedSitesFile() throws IOException {
			feedSitesFileModified = feedSitesFile.toFile().lastModified();
			
			return new ObjectMapper().readValue(readFeedSitesConfig(feedSitesFile), Entries.class).getEntries().stream()
				.collect(toMap(entry -> entry.getAlias(), entry -> entry));
	}
	
	@VisibleForTesting
	protected String readFeedSitesConfig(@Nonnull Path feedSitesFile) throws IOException {
		 return FileUtils.readFileToString(feedSitesFile.toFile());
	}

}