package com.atex.plugins.newsstand.updater;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.atex.plugins.newsstand.NewsstandRenderController;
import com.atex.plugins.newsstand.catalog.data.Catalog;
import com.atex.plugins.newsstand.client.ViewerClient;
import com.atex.plugins.newsstand.client.ViewerClientFactory;
import com.polopoly.cache.CacheKey;
import com.polopoly.cache.SynchronizedUpdateCache;

/**
 * Updating thread.
 */
public class UpdaterThread extends Thread {

    private static final Logger LOGGER = Logger.getLogger(UpdaterThread.class.getName());

    public static final int CACHE_TIMEOUT = 30 * 60 * 1000;

    private final SynchronizedUpdateCache updateCache;
    private final String catalogName;

    public UpdaterThread(final SynchronizedUpdateCache updateCache,
                         final String catalogName) {

        super("updater-thread-" + catalogName);

        this.updateCache = updateCache;
        this.catalogName = catalogName;
    }

    @Override
    public void run() {

        final CacheKey cacheKey = NewsstandRenderController.getCacheKey(catalogName);

        try {
            final ViewerClient viewerClient = ViewerClientFactory.getInstance().createClient();

            final Catalog oldCatalog = (Catalog) updateCache.get(cacheKey);
            final Catalog catalog = viewerClient.getCatalogIssues(catalogName);
            if ((oldCatalog == null) || !oldCatalog.getMd5().equals(catalog.getMd5())) {
                LOGGER.info("put " + catalog + " into cache " + cacheKey.toString());
                updateCache.put(cacheKey, catalog, CACHE_TIMEOUT);
            } else {
                updateCache.release(cacheKey, CACHE_TIMEOUT);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error getting catalog '" + catalogName + "': " + e.getMessage(), e);
        }

    }

}
