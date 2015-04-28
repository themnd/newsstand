package com.atex.plugins.newsstand.ws;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atex.plugins.newsstand.ConfigurationPolicy;
import com.atex.plugins.newsstand.catalog.data.Catalog;
import com.atex.plugins.newsstand.controller.NewsstandRenderController;
import com.google.gson.Gson;
import com.polopoly.cache.CacheKey;
import com.polopoly.cache.SynchronizedUpdateCache;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.policy.PolicyCMServer;

/**
 * Jersey resource for publications.
 */
@Path("/catalog/{name}")
public class CatalogResource {

    private static final Logger LOGGER = Logger.getLogger(CatalogResource.class.getName());

    public static final int MAX_AGE_5_MINUTES = 300;

    @Context
    private CmClient cmClient;

    @Context
    private SynchronizedUpdateCache updateCache;

    private Gson gson = new Gson();

    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getCatalogDetail(@PathParam("name") final String name) {
        final Catalog catalog = getCatalog(name);
        if (catalog == null) {
            return Response.status(Status.NOT_FOUND).cacheControl(noCache()).build();
        } else {
            return Response.ok(catalog).cacheControl(doCache(MAX_AGE_5_MINUTES)).build();
        }
    }

    private Catalog getCatalog(final String catalogName) {
        try {
            final ConfigurationPolicy configuration = getConfigurationPolicy();
            final List<String> catalogs = configuration.getCatalogs();
            if (updateCache != null) {
                final CacheKey cacheKey = NewsstandRenderController.getCacheKey(catalogName);
                try {
                    return (Catalog) updateCache.get(cacheKey);
                } catch (Exception e) {
                    LOGGER.severe("Cannot get catalog '" + catalogName + "': " + e.getMessage());
                }
            }
        } catch (CMException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    private CacheControl noCache() {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(true);
        cacheControl.setNoStore(true);
        cacheControl.setMustRevalidate(true);
        cacheControl.setProxyRevalidate(true);
        cacheControl.setMaxAge(0);
        cacheControl.setSMaxAge(0);
        return cacheControl;
    }

    private CacheControl doCache(final int maxAge) {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setNoCache(false);
        cacheControl.setNoStore(false);
        cacheControl.setMustRevalidate(false);
        cacheControl.setProxyRevalidate(false);
        cacheControl.setMaxAge(maxAge);
        cacheControl.setSMaxAge(maxAge);
        return cacheControl;
    }

    private ConfigurationPolicy getConfigurationPolicy() throws CMException {
        final PolicyCMServer cmServer = getCMServer();
        return (ConfigurationPolicy) cmServer.getPolicy(new ExternalContentId(ConfigurationPolicy.CONFIG_EXTERNAL_ID));
    }

    private PolicyCMServer getCMServer() {
        return cmClient.getPolicyCMServer();
    }


}