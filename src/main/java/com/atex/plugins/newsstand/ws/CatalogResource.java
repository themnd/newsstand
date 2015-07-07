package com.atex.plugins.newsstand.ws;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atex.plugins.newsstand.ConfigurationPolicy;
import com.atex.plugins.newsstand.catalog.data.Catalog;
import com.atex.plugins.newsstand.catalog.data.Issue;
import com.atex.plugins.newsstand.catalog.data.Publication;
import com.atex.plugins.newsstand.controller.NewsstandRenderController;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
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

    @GET
    @Path("/publication")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getPublicationDetail(@PathParam("name") final String name,
                                         @QueryParam("issueId") final String issueId,
                                         @QueryParam("issueCode") final String issueCode,
                                         @QueryParam("includeIssues") final Boolean includeIssuesParam) {

        final boolean useIssueId = !Strings.isNullOrEmpty(issueId);
        final boolean useIssueCode = !Strings.isNullOrEmpty(issueCode);
        final boolean includeIssues = Optional.fromNullable(includeIssuesParam).or(true);
        if (useIssueId || useIssueCode) {
            final Catalog catalog = getCatalog(name);
            if (catalog != null) {
                if (catalog.getPublications() != null) {
                    for (final Publication publication : catalog.getPublications()) {
                        if (publication.getIssues() != null) {
                            for (final Issue issue : publication.getIssues()) {
                                if (useIssueId) {
                                    if (issue.getId().equals(issueId)) {
                                        if (includeIssues) {
                                            return Response.ok(publication)
                                                           .cacheControl(doCache(MAX_AGE_5_MINUTES))
                                                           .build();
                                        } else {
                                            return Response.ok(clonePublicationWithoutIssues(publication))
                                                           .cacheControl(doCache(MAX_AGE_5_MINUTES))
                                                           .build();
                                        }
                                    }
                                }
                                if (useIssueCode) {
                                    if (issue.getIssueCode().equals(issueCode)) {
                                        if (includeIssues) {
                                            return Response.ok(publication)
                                                           .cacheControl(doCache(MAX_AGE_5_MINUTES))
                                                           .build();
                                        } else {
                                            return Response.ok(clonePublicationWithoutIssues(publication))
                                                           .cacheControl(doCache(MAX_AGE_5_MINUTES))
                                                           .build();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return Response.status(Status.NOT_FOUND).cacheControl(noCache()).build();
    }

    @GET
    @Path("/last-newspaper")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getLastNewspaperIssue(@PathParam("name") final String name) {

        final Catalog catalog = getCatalog(name);
        if (catalog != null) {
            if (catalog.getPublications() != null) {
                try {
                    final ConfigurationPolicy configurationPolicy = getConfigurationPolicy();
                    final List<String> newspaperCodes = configurationPolicy.getNewspapers();
                    for (final Publication publication : catalog.getPublications()) {
                        if (newspaperCodes.contains(publication.getId())) {
                            final List<Issue> issues = publication.getIssues();
                            if (issues.size() > 0) {
                                return Response.ok(issues.get(0))
                                               .cacheControl(doCache(MAX_AGE_5_MINUTES))
                                               .build();
                            }

                        }
                    }
                } catch (CMException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }

        return Response.status(Status.NOT_FOUND).cacheControl(noCache()).build();
    }

    private Publication clonePublicationWithoutIssues(final Publication publication) {
        return new Publication(
                publication.getPublisher(),
                publication.getId(),
                publication.getName(),
                publication.getDefaultLanguage()
        );
    }

    private Catalog getCatalog(final String catalogName) {
        try {
            final ConfigurationPolicy configuration = getConfigurationPolicy();
            final List<String> catalogs = configuration.getCatalogs();
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
