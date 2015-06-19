package com.atex.plugins.newsstand.util;

import java.util.logging.Logger;

import com.atex.plugins.newsstand.catalog.data.Catalog;
import com.atex.plugins.newsstand.catalog.data.Issue;
import com.atex.plugins.newsstand.catalog.data.Publication;
import com.atex.plugins.newsstand.controller.NewsstandRenderController;
import com.polopoly.cache.CacheKey;
import com.polopoly.cache.SynchronizedUpdateCache;
import com.polopoly.util.StringUtil;

/**
 * Util class to deal with a catalog.
 *
 * @author mnova
 */
public class CatalogUtil {

    private static final Logger LOGGER = Logger.getLogger(CatalogUtil.class.getName());

    private final SynchronizedUpdateCache updateCache;

    public CatalogUtil(final SynchronizedUpdateCache updateCache) {
        this.updateCache = updateCache;
    }

    public Catalog getCatalog(final String catalogName) {
        if (updateCache != null) {
            final CacheKey cacheKey = NewsstandRenderController.getCacheKey(catalogName);
            Catalog catalog = null;
            try {
                catalog = (Catalog) updateCache.get(cacheKey);
                return catalog;
            } catch (Exception e) {
                LOGGER.severe("Cannot get catalog '" + catalogName + "': " + e.getMessage());
            } finally {
                if (catalog == null) {
                    updateCache.release(cacheKey, NewsstandRenderController.CACHE_RELEASE_TIMEOUT);
                }
            }
        }
        return null;
    }

    public Issue getIssueFromCatalog(final String catalogName, final String issueCode) {
        if (updateCache != null) {
            final CacheKey cacheKey = NewsstandRenderController.getCacheKey(catalogName);
            try {
                final Catalog catalog = (Catalog) updateCache.get(cacheKey);
                try {
                    if (catalog != null) {
                        for (final Publication publication : catalog.getPublications()) {
                            for (final Issue issue : publication.getIssues()) {
                                if (StringUtil.equalsIgnoreCase(issue.getIssueCode(), issueCode)) {
                                    return issue;
                                }
                            }
                        }
                    }
                } finally {
                    if (catalog == null) {
                        updateCache.release(cacheKey, NewsstandRenderController.CACHE_RELEASE_TIMEOUT);
                    }
                }
            } catch (Exception e) {
                LOGGER.severe("Cannot get catalog '" + catalogName + "': " + e.getMessage());
            }
        }
        return null;
    }

}
