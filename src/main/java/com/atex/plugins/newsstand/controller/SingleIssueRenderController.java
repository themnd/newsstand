package com.atex.plugins.newsstand.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.atex.plugins.newsstand.ConfigurationPolicy;
import com.atex.plugins.newsstand.SingleIssuePolicy;
import com.atex.plugins.newsstand.catalog.data.Catalog;
import com.atex.plugins.newsstand.catalog.data.Issue;
import com.atex.plugins.newsstand.catalog.data.Publication;
import com.atex.plugins.newsstand.client.ViewerClient;
import com.atex.plugins.newsstand.client.ViewerClientFactory;
import com.atex.plugins.newsstand.util.CatalogUtil;
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
public class SingleIssueRenderController extends RenderControllerBase {

    private static final Logger LOGGER = Logger.getLogger(SingleIssueRenderController.class.getName());

    @Override
    public void populateModelBeforeCacheKey(final RenderRequest request, final TopModel m, final ControllerContext context) {

        super.populateModelBeforeCacheKey(request, m, context);

        try {
            final ConfigurationPolicy config = getConfiguration(context);
            final SingleIssuePolicy policy = (SingleIssuePolicy) ModelPathUtil.getBean(context.getContentModel());

            final String catalogName = policy.getCatalog();
            final String issueNumber = policy.getIssue();

            try {
                final Catalog catalog = getCatalogFromCache(catalogName, false);
                if (catalog != null) {
                    boolean found = false;
                    for (final Publication publication : catalog.getPublications()) {
                        for (final Issue issue : publication.getIssues()) {
                            if (issue.getIssueCode().equals(issueNumber)) {
                                found = true;

                                ModelPathUtil.set(m.getLocal(), "catalog", catalogName);
                                ModelPathUtil.set(m.getLocal(), "issue", issue);
                                ModelPathUtil.set(m.getLocal(), "publication", publication);
                                break;
                            }
                        }
                        if (found) {
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.severe("Error processing catalog '" + catalogName + "': " + e.getMessage());
            }
        } catch (CMException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private ConfigurationPolicy getConfiguration(final ControllerContext context) throws CMException {

        final PolicyCMServer policyCMServer = getCmClient(context).getPolicyCMServer();
        return (ConfigurationPolicy) policyCMServer.getPolicy(
                new ExternalContentId(ConfigurationPolicy.CONFIG_EXTERNAL_ID));

    }

    private Catalog getCatalogFromCache(final String catalogName,
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
