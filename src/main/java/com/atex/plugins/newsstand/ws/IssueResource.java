package com.atex.plugins.newsstand.ws;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.atex.plugins.newsstand.ConfigurationPolicy;
import com.atex.plugins.newsstand.catalog.data.Catalog;
import com.atex.plugins.newsstand.catalog.data.Issue;
import com.atex.plugins.newsstand.catalog.data.Publication;
import com.atex.plugins.newsstand.client.ViewerClient;
import com.atex.plugins.newsstand.client.ViewerClientFactory;
import com.atex.plugins.newsstand.client.data.AuthData;
import com.atex.plugins.newsstand.util.CatalogUtil;
import com.polopoly.cm.ExternalContentId;
import com.polopoly.cm.client.CMException;
import com.polopoly.cm.client.CmClient;
import com.polopoly.cm.policy.PolicyCMServer;
import com.polopoly.util.StringUtil;

/**
 * Jersey resource for issues.
 */
@Path("/issue")
public class IssueResource {

    private static final Logger LOGGER = Logger.getLogger(IssueResource.class.getName());

    public static final int MAX_AGE_5_MINUTES = 300;
    private static final String NEWSSTAND_CACHE_DIR = "newsstand.cacheDir";
    private static final String ISSUES_CACHE_DIR = "issues";

    @Context
    private CmClient cmClient;

    private final CatalogUtil catalogUtil = CatalogUtil.getInstance();

    private File cacheLocation = new File(new File(getNewsstandCacheDir()), ISSUES_CACHE_DIR);

    @PostConstruct
    private void initCacheDirs() {
        if (!cacheLocation.exists()) {
            cacheLocation.mkdirs();
        }
    }

    @GET
    @Path("{issueCode}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getIssueDetail(@PathParam("issueCode") final String issueCode) {
        final Issue issue = getIssueFromCatalogs(issueCode);
        if (issue == null) {
            return Response.status(Status.NOT_FOUND).cacheControl(noCache()).build();
        } else {
            return Response.ok(issue).cacheControl(doCache(MAX_AGE_5_MINUTES)).build();
        }
    }

    @GET
    @Path("{issueCode}/cover/{resolution}.jpg")
    @Produces({ "image/jpeg" })
    public Response getCover(@PathParam("issueCode") final String issueCode,
                             @PathParam("resolution") final String resolution) {

        final Issue issue = getIssueFromCatalogs(issueCode);
        if (issue == null) {
            return Response.status(Status.NOT_FOUND).cacheControl(noCache()).build();
        }

        try {
            final int resx = Integer.parseInt(resolution);

            final byte[] cover;
            final File cacheDir = getIssueCacheDir(issue);
            final File coverFile = new File(cacheDir, "cover-" + resx + ".jpg");
            if (coverFile.exists()) {
                try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    Files.copy(Paths.get(coverFile.getAbsolutePath()), bos);
                    IOUtils.closeQuietly(bos);
                    cover = bos.toByteArray();
                }
                touch(coverFile);
            } else {
                final ViewerClient vc = createViewerClient();
                cover = vc.getIssueCover(issueCode, resx);

                cacheDir.mkdirs();
                com.atex.plugins.newsstand.util.FileUtils.createFile(cover, coverFile);
            }
            return Response.ok(cover).cacheControl(doCache(MAX_AGE_5_MINUTES)).build();
        } catch (NumberFormatException | IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return Response.status(Status.BAD_REQUEST).cacheControl(noCache()).build();
        }
    }

    @GET
    @Path("{issueCode}/page/{page}.pdf")
    @Produces({ "application/pdf" })
    public Response getPDFPage(@PathParam("issueCode") final String issueCode,
                               @PathParam("page") final String page) {

        final Issue issue = getIssueFromCatalogs(issueCode);
        if (issue == null) {
            return Response.status(Status.NOT_FOUND).cacheControl(noCache()).build();
        }

        try {
            final int pageNumber = Integer.parseInt(page);


            final byte[] pdf;
            final File cacheDir = getIssueCacheDir(issue);
            final File pdfFile = new File(cacheDir, "page-" + pageNumber + ".pdf");
            if (pdfFile.exists()) {
                try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    Files.copy(Paths.get(pdfFile.getAbsolutePath()), bos);
                    IOUtils.closeQuietly(bos);
                    pdf = bos.toByteArray();
                }
                touch(pdfFile);
            } else {
                final ViewerClient vc = createViewerClient();
                pdf = vc.getIssuePDFPage(issueCode, pageNumber);

                cacheDir.mkdirs();
                com.atex.plugins.newsstand.util.FileUtils.createFile(pdf, pdfFile);
            }
            return Response.ok(pdf).cacheControl(doCache(MAX_AGE_5_MINUTES)).build();
        } catch (NumberFormatException | IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return Response.status(Status.BAD_REQUEST).cacheControl(noCache()).build();
        }
    }

    @GET
    @Path("{issueCode}.pdf")
    @Produces({ "application/pdf" })
    public Response getPDF(@PathParam("issueCode") final String issueCode) {

        final Issue issue = getIssueFromCatalogs(issueCode);
        if (issue == null) {
            return Response.status(Status.NOT_FOUND).cacheControl(noCache()).build();
        }

        try {
            final byte[] pdf;
            final File cacheDir = getIssueCacheDir(issue);
            final File pdfFile = new File(cacheDir, "issue.pdf");
            if (pdfFile.exists()) {
                try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                    Files.copy(Paths.get(pdfFile.getAbsolutePath()), bos);
                    IOUtils.closeQuietly(bos);
                    pdf = bos.toByteArray();
                }
                touch(pdfFile);
            } else {
                final ViewerClient vc = createViewerClient();
                pdf = vc.getIssuePDF(issueCode);

                cacheDir.mkdirs();
                com.atex.plugins.newsstand.util.FileUtils.createFile(pdf, pdfFile);
            }
            return Response.ok(pdf).cacheControl(doCache(MAX_AGE_5_MINUTES)).build();
            //response = response.header("Content-Disposition", "attachment; filename=" + pdfFile.getName());
        } catch (NumberFormatException | IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return Response.status(Status.BAD_REQUEST).cacheControl(noCache()).build();
        }
    }

    @POST
    @Path("{issueCode}/login")
    @Produces({ MediaType.TEXT_PLAIN })
    public Response getLoginUrl(@PathParam("issueCode") final String issueCode,
                                @QueryParam("catalog") final String catalogName) {

        Status status = Status.BAD_REQUEST;

        if (validateCatalog(catalogName)) {

            final Issue issue = getIssueFromCatalog(catalogName, issueCode);
            if (issue == null) {
                LOGGER.warning("Cannot find issue " + issueCode + " in catalog " + catalogName);
                status = Status.NOT_FOUND;
            } else {
                try {
                    final ViewerClient vc = createViewerClient();
                    final AuthData auth = vc.login(catalogName, issueCode);
                    if (auth.getUrl() != null) {
                        return Response.ok(auth.getUrl()).cacheControl(noCache()).build();
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }

        return Response.status(status).cacheControl(noCache()).build();
    }

    private void touch(final File file) {
        try {
            FileUtils.touch(file);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }
    }

    private boolean validateCatalog(final String catalogName) {
        try {
            checkNotNull(catalogName);

            final ConfigurationPolicy configuration = getConfigurationPolicy();
            final List<String> catalogs = configuration.getCatalogs();
            for (final String name : catalogs) {
                if (StringUtil.equals(name, catalogName)) {
                    return true;
                }
            }

            LOGGER.warning("Cannot find catalog " + catalogName);

        } catch (CMException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return false;
    }

    private ViewerClient createViewerClient() {
        return ViewerClientFactory.getInstance().createClient();
    }

    private Issue getIssueFromCatalog(final String catalogName, final String issueCode) {
        final Catalog catalog = catalogUtil.getCatalog(catalogName);
        if (catalog != null) {
            for (final Publication publication : catalog.getPublications()) {
                for (final Issue issue : publication.getIssues()) {
                    if (StringUtil.equalsIgnoreCase(issue.getIssueCode(), issueCode)) {
                        return issue;
                    }
                }
            }
        }
        return null;
    }

    private Issue getIssueFromCatalogs(final String issueCode) {
        try {
            final ConfigurationPolicy configuration = getConfigurationPolicy();
            final List<String> catalogs = configuration.getCatalogs();
            for (final String catalogName : catalogs) {
                final Issue issue = getIssueFromCatalog(catalogName, issueCode);
                if (issue != null) {
                    return issue;
                }
            }

            LOGGER.warning("Cannot find issue " + issueCode);
        } catch (CMException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    private File getIssueCacheDir(final Issue issue) {
        checkNotNull(issue);

        final Date date = getLatestIssueDate(issue);
        File dir = new File(cacheLocation, issue.getIssueCode());
        if (date != null) {
            return new File(dir, Long.toString(date.getTime()));
        }
        return dir;
    }

    private Date getLatestIssueDate(final Issue issue) {
        checkNotNull(issue);
        Date date = issue.getLatestExport();
        if (date == null) {
            date = issue.getReleaseDate();
        }
        return date;
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

    private String getNewsstandCacheDir() {
        String location = System.getProperty(NEWSSTAND_CACHE_DIR);
        if (location == null) {
            location = System.getenv(NEWSSTAND_CACHE_DIR);
        }
        if (location == null) {
            location = FileUtils.getTempDirectoryPath();
        }
        return location;
    }

}
