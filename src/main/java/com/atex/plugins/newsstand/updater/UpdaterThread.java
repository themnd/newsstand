package com.atex.plugins.newsstand.updater;

import java.util.logging.Logger;

import com.atex.plugins.newsstand.controller.NewsstandRenderController;
import com.polopoly.cache.SynchronizedUpdateCache;

/**
 * Updating thread.
 */
public class UpdaterThread extends Thread {

    private static final Logger LOGGER = Logger.getLogger(UpdaterThread.class.getName());

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
        NewsstandRenderController.getCatalogFromCache(updateCache, catalogName, true);
    }

}
