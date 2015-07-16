package com.atex.plugins.newsstand.controller;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.atex.plugins.newsstand.ConfigurationPolicy;
import com.atex.plugins.newsstand.NewsstandPolicy;
import com.atex.plugins.newsstand.catalog.data.Catalog;
import com.atex.plugins.newsstand.catalog.data.Publication;
import com.atex.plugins.newsstand.client.ViewerClient;
import com.atex.plugins.newsstand.client.ViewerClientFactory;
import com.atex.plugins.newsstand.util.CatalogUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.polopoly.application.Application;
import com.polopoly.cache.LRUSynchronizedUpdateCache;
import com.polopoly.cache.SynchronizedUpdateCache;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.model.ModelPathUtil;
import com.polopoly.render.RenderRequest;
import com.polopoly.siteengine.dispatcher.ControllerContext;
import com.polopoly.siteengine.model.TopModel;
import com.polopoly.siteengine.mvc.RenderControllerBase;

/**
 * Render controller for the newsstand.
 *
 * @author mnova
 */
public class NewsstandRenderController extends RenderControllerBase {

    private static final Logger LOGGER = Logger.getLogger(NewsstandRenderController.class.getName());

    public static final int CACHE_TIMEOUT = 30 * 60 * 1000;
    public static final int CACHE_RELEASE_TIMEOUT = 2 * 1000;

    @Override
    public void populateModelBeforeCacheKey(final RenderRequest request, final TopModel m, final ControllerContext context) {

        super.populateModelBeforeCacheKey(request, m, context);

        try {
            final ConfigurationPolicy config = getConfiguration(context);
            final List<String> availableCatalogs = config.getCatalogs();
            final NewsstandPolicy policy = (NewsstandPolicy) ModelPathUtil.getBean(context.getContentModel());
            final List<String> showCatalogs = policy.getShowCatalogs();

            final List<String> catalogs = Lists.newArrayList();

            // add only catalogs configured on the element that
            // are available globally.

            for (final String catalog : showCatalogs) {
                if (availableCatalogs.contains(catalog)) {
                    catalogs.add(catalog);
                } else {
                    LOGGER.warning("Catalog " + catalog + " has not been configured in the plugin configuration");
                }
            }
            if (catalogs.size() == 0) {
                catalogs.addAll(availableCatalogs);
            }

            ModelPathUtil.set(m.getLocal(), "catalogs", catalogs);

            final Map<String, CatalogData> catalogInfo = Maps.newHashMap();
            for (final String catalogName : catalogs) {
                try {
                    final Catalog catalog = getCatalogFromCache(catalogName, false);
                    final CatalogData data = new CatalogData(catalogName, catalog);
                    if (policy.isShowNewspapers()) {
                        data.getNewspapers().addAll(
                                filterPublications(catalog.getPublications(), config.getNewspapers()));
                    }
                    if  (policy.isShowMagazines()) {
                        data.getMagazines().addAll(
                                filterPublications(catalog.getPublications(), config.getMagazines()));
                    }
                    if (policy.isShowSeasonals()) {
                        data.getSeasonals().addAll(
                                filterPublications(catalog.getPublications(), config.getSeasonals()));
                    }
                    if (policy.isShowSpecials()) {
                        data.getSpecials().addAll(
                                filterPublications(catalog.getPublications(), config.getSpecials()));
                    }
                    catalogInfo.put(catalogName, data);
                } catch (Exception e) {
                    LOGGER.severe("Error processing catalog '" + catalogName + "': " + e.getMessage());
                }
            }
            ModelPathUtil.set(m.getLocal(), "catalogInfo", catalogInfo);
        } catch (CMException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private List<Publication> filterPublications(final List<Publication> publications,
                                                 final List<String> codes) {

        final List<Publication> results = Lists.newArrayList();

        if (codes != null && codes.size() > 0 && publications.size() > 0) {
            for (final Publication publication : publications) {
                if (codes.contains(publication.getId())) {
                    results.add(publication);
                }
            }
        }

        return results;
    }

    private SynchronizedUpdateCache getCache(final ControllerContext context) {
        final Application application = context.getApplication();
        if (application == null) {
            LOGGER.log(Level.INFO, "No application configured.");
            return null;
        }
        return (SynchronizedUpdateCache) application.getApplicationComponent(
                LRUSynchronizedUpdateCache.DEFAULT_COMPOUND_NAME);
    }

    private ConfigurationPolicy getConfiguration(final ControllerContext context) throws CMException {

        final PolicyCMServer policyCMServer = getCmClient(context).getPolicyCMServer();
        return (ConfigurationPolicy) policyCMServer.getPolicy(
                new ExternalContentId(ConfigurationPolicy.CONFIG_EXTERNAL_ID));

    }

    public static Catalog getCatalogFromCache(final String catalogName,
                                              final boolean checkUpdates) {

        Catalog catalog = null;
        try {
            final CatalogUtil catalogUtil = CatalogUtil.getInstance();
            catalog = catalogUtil.getCatalog(catalogName);
            if (catalog == null) {
                catalog = createClient().getCatalogIssues(catalogName);
                if (catalog != null) {
                    catalogUtil.putCatalog(catalogName, catalog);
                }
            } else if (checkUpdates) {
                final Catalog newCatalog = createClient().getCatalogIssues(catalogName);
                if (newCatalog != null) {
                    if (!newCatalog.getMd5().equals(catalog.getMd5())) {
                        catalogUtil.putCatalog(catalogName, newCatalog);
                        catalog = newCatalog;
                    }
                }
            }
        } catch (Throwable e) {
            LOGGER.log(Level.SEVERE, "Error getting catalog '" + catalogName + "': " + e.getMessage(), e);
        }

        return catalog;
    }

    private static ViewerClient createClient() {
        return ViewerClientFactory.getInstance().createClient();
    }

}
