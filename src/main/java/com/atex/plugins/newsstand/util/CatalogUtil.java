package com.atex.plugins.newsstand.util;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.atex.plugins.newsstand.catalog.data.Catalog;
import com.atex.plugins.newsstand.catalog.data.Issue;
import com.atex.plugins.newsstand.catalog.data.Publication;
import com.google.common.collect.Maps;
import com.polopoly.util.StringUtil;

/**
 * Util class to deal with a catalog.
 *
 * @author mnova
 */
public class CatalogUtil {

    private static final Logger LOGGER = Logger.getLogger(CatalogUtil.class.getName());

    private static final CatalogUtil INSTANCE = new CatalogUtil();

    private Map<String, Catalog> catalogMap = Maps.newConcurrentMap();

    private CatalogUtil() {
    }

    public static CatalogUtil getInstance() {
        return INSTANCE;
    }

    public Catalog getCatalog(final String catalogName) {
        final Catalog catalog = catalogMap.get(catalogName);
        if (catalog == null) {
            LOGGER.log(Level.FINE, "catalog " + catalogName + " not found");
        }
        return catalog;
    }

    public Issue getIssueFromCatalog(final String catalogName, final String issueCode) {
        final Catalog catalog = getCatalog(catalogName);
        if (catalog != null) {
            for (final Publication publication : catalog.getPublications()) {
                for (final Issue issue : publication.getIssues()) {
                    if (StringUtil.equalsIgnoreCase(issue.getIssueCode(), issueCode)) {
                        return issue;
                    }
                }
            }
            LOGGER.log(Level.FINE, "Issue " + issueCode + " in catalog " + catalogName + " not found");
        }
        return null;
    }

    public void putCatalog(final String catalogName, final Catalog catalog) {
        LOGGER.info("put " + catalog + " into cache " + catalogName);
        catalogMap.put(catalogName, catalog);
    }
}
