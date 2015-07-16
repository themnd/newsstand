package com.atex.plugins.newsstand.updater;

import com.atex.plugins.newsstand.controller.NewsstandRenderController;

/**
 * Updating thread.
 */
public class UpdaterThread extends Thread {

    private final String catalogName;

    public UpdaterThread(final String catalogName) {

        super("updater-thread-" + catalogName);

        this.catalogName = catalogName;
    }

    @Override
    public void run() {
        NewsstandRenderController.getCatalogFromCache(catalogName, true);
    }

}
