package com.atex.plugins.newsstand.controller;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.atex.plugins.newsstand.ConfigurationPolicy;
import com.atex.plugins.newsstand.catalog.data.Catalog;
import com.atex.plugins.newsstand.catalog.data.Publication;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.polopoly.application.Application;
import com.polopoly.cache.CacheKey;
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

    @Override
    public void populateModelBeforeCacheKey(final RenderRequest request, final TopModel m, final ControllerContext context) {

        super.populateModelBeforeCacheKey(request, m, context);

        try {
            final ConfigurationPolicy config = getConfiguration(context);
            final List<String> catalogs = config.getCatalogs();
            ModelPathUtil.set(m.getLocal(), "catalogs", catalogs);

            final SynchronizedUpdateCache updateCache = getCache(context);
            if (updateCache != null) {
                final Map<String, CatalogData> catalogInfo = Maps.newHashMap();
                for (final String catalogName : catalogs) {
                    try {
                        final Catalog catalog = (Catalog) updateCache.get(getCacheKey(catalogName));
                        final CatalogData data = new CatalogData(catalogName, catalog);
                        data.getNewspapers().addAll(filterPublications(catalog.getPublications(), config.getNewspapers()));
                        data.getMagazines().addAll(filterPublications(catalog.getPublications(), config.getMagazines()));
                        data.getCollaterals().addAll(filterPublications(catalog.getPublications(), config.getCollaterals()));
                        catalogInfo.put(catalogName, data);
                    } catch (Exception e) {
                        LOGGER.severe("Error processing catalog '" + catalogName + "': " + e.getMessage());
                    }
                }
                ModelPathUtil.set(m.getLocal(), "catalogInfo", catalogInfo);
            }
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

    public static CacheKey getCacheKey(final String catalogName) {
        return new CacheKey(NewsstandRenderController.class, "catalog." + catalogName);
    }

}
