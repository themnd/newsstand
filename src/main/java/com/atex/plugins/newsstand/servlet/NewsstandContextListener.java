package com.atex.plugins.newsstand.servlet;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import com.atex.plugins.newsstand.ConfigurationPolicy;
import com.atex.plugins.newsstand.client.ViewerClientConfiguration;
import com.atex.plugins.newsstand.client.ViewerClientConfigurationFactory;
import com.atex.plugins.newsstand.client.ViewerClientFactory;
import com.atex.plugins.newsstand.updater.UpdaterThread;
import com.polopoly.application.Application;
import com.polopoly.application.servlet.ApplicationNameNotFoundException;
import com.polopoly.application.servlet.ApplicationServletUtil;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.VersionInfo;
import com.polopoly.cm.VersionedContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.client.CmClientBase;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.util.StringUtil;

/**
 * Context listener that start the update thread.
 */
public class NewsstandContextListener implements javax.servlet.ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(NewsstandContextListener.class.getName());

    private ScheduledExecutorService scheduler = null;

    @Override
    public void contextInitialized(final ServletContextEvent event) {
        final ServletContext servletContext = event.getServletContext();
        try {
            final String appName = ApplicationServletUtil.getApplicationName(servletContext);
            if (StringUtil.equals(appName, "preview") || StringUtil.equals(appName, "front")) {
                LOGGER.info("Init newsstand for " + appName);
                initUpdater(servletContext);
            }
        } catch (ApplicationNameNotFoundException e) {
            LOGGER.fine("no app name for context path " + servletContext.getContextPath());
        } catch (CMException e) {
            LOGGER.log(Level.SEVERE, "Cannot initialize newsstand: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent event) {
        destroyUpdater();
    }

    private void initUpdater(final ServletContext servletContext) throws CMException {
        final Application application = ApplicationServletUtil.getApplication(servletContext);
        final CmClient cmClient = (CmClient) application.getApplicationComponent(CmClientBase.DEFAULT_COMPOUND_NAME);

        final PolicyCMServer cmServer = cmClient.getPolicyCMServer();

        // in a local development environment, when you apply configuration changes
        // it seems that the configuration policy returned by the CMServer is the
        // oldest one, probably because the content cache has not been updated yet.
        // So to force the retrieval of the latest committed, we will use the
        // uncached call getVersionInfos which will return all the policy versions
        // and we will use the first committed one.
        //
        // [mnova - 20160303]

        final VersionedContentId contentId = cmServer.findContentIdByExternalId(new ExternalContentId(ConfigurationPolicy.CONFIG_EXTERNAL_ID));
        final VersionInfo[] versionInfo = cmServer.getVersionInfos(contentId.getLatestVersionId());

        LOGGER.info("loading configuration");

        ConfigurationPolicy configurationPolicy = null;
        if (versionInfo != null) {
            for (int idx = versionInfo.length - 1; idx >= 0; idx--) {
                final VersionInfo vi = versionInfo[idx];
                LOGGER.info(vi.toString());
                try {
                    if (vi.isCommitted()) {
                        final VersionedContentId vid = new VersionedContentId(contentId, vi.getVersion());
                        configurationPolicy = (ConfigurationPolicy) cmServer.getPolicy(vid);
                        break;
                    }
                } catch (CMException e) {
                    LOGGER.log(Level.SEVERE, "Cannot load version " + vi.getVersion(), e);
                }
            }
        }

        if (configurationPolicy == null) {
            LOGGER.info("no configuration found, loading default one");
            configurationPolicy = (ConfigurationPolicy) cmServer.getPolicy(contentId);
        }

        final ViewerClientConfiguration configuration = ViewerClientConfigurationFactory.from(configurationPolicy);
        ViewerClientFactory.initInstance(configuration);

        final List<String> catalogs = configurationPolicy.getCatalogs();

        scheduler = Executors.newScheduledThreadPool(1);
        for (final String catalogName : catalogs) {
            scheduler.scheduleAtFixedRate(
                    new UpdaterThread(catalogName),
                    0, 1, TimeUnit.HOURS);
        }
    }

    private void destroyUpdater() {
        if (scheduler != null) {
            try {
                scheduler.shutdownNow();
                LOGGER.info("Wait 30 seconds for threads to stop");
                scheduler.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Tasks do not stop after 30 seconds!");
                scheduler.shutdown();
            }
            LOGGER.info("threads terminated.");
        }
    }

}
