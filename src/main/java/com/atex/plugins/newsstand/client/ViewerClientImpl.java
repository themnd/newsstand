package com.atex.plugins.newsstand.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.atex.plugins.newsstand.catalog.CatalogParser;
import com.atex.plugins.newsstand.catalog.data.Catalog;
import com.atex.plugins.newsstand.client.data.AuthData;
import com.google.gson.Gson;

/**
 * Implementation for {@link ViewerClient}.
 */
public class ViewerClientImpl implements ViewerClient {

    private static final Logger LOGGER = Logger.getLogger(ViewerClientImpl.class.getName());

    private final Gson gson = new Gson();

    private final ViewerClientConfiguration configuration;
    private final HttpClient httpClient;

    public ViewerClientImpl(final ViewerClientConfiguration configuration, final HttpClient httpClient) {
        this.configuration = checkNotNull(configuration);
        this.httpClient = httpClient;

        LOGGER.info("using configuration: " + configuration);
    }

    @Override
    public Catalog getCatalogIssues(final String catalog) throws IOException {

        LOGGER.info("getCatalogIssues: " + catalog);

        final String url = createCatalogIssuesUrl(catalog);

        final File file = File.createTempFile("catalog.", ".sqllite");
        try {
            try (final FileOutputStream fos = new FileOutputStream(file)) {
                httpCall(url, fos);
                IOUtils.closeQuietly(fos);
            }

            return parseCatalog(file);
        } finally {
            FileUtils.deleteQuietly(file);
        }
    }

    @Override
    public AuthData login(final String catalog, final String issueCode) throws IOException {

        LOGGER.info("login for catalog " + catalog + " and issue " + issueCode);

        final String url = createLoginUrl(catalog, issueCode);
        final String content = httpCall(url);

        if (content != null) {
            return gson.fromJson(content, AuthData.class);
        }
        return new AuthData();
    }

    @Override
    public byte[] getIssueCover(final String issueCode, final int resolution) throws IOException {

        LOGGER.info("getIssueCover for issue " + issueCode + " with resolution " + resolution);

        final String url = createIssueCoverUrl(issueCode, resolution);

        try (final ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
            httpCall(url, fos);
            IOUtils.closeQuietly(fos);
            return fos.toByteArray();
        }
    }

    @Override
    public byte[] getIssuePDFPage(final String issueCode, final int pageNumber) throws IOException {

        LOGGER.info("getIssuePDF for issue " + issueCode + " and page " + pageNumber);

        final String url = createIssuePDFUrl(issueCode, pageNumber);

        try (final ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
            httpCall(url, fos);
            IOUtils.closeQuietly(fos);
            return fos.toByteArray();
        }
    }

    @Override
    public byte[] getIssuePDF(final String issueCode) throws IOException {

        LOGGER.info("getIssuePDF for issue " + issueCode);

        final String url = createIssuePDFUrl(issueCode);

        try (final ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
            httpCall(url, fos);
            IOUtils.closeQuietly(fos);
            return fos.toByteArray();
        }
    }

    Catalog parseCatalog(final File file) throws IOException {
        final CatalogParser parser = new CatalogParser(file);
        return parser.open();
    }

    String createIssuePDFUrl(final String issueCode, final int pageNumber) {
        return String.format("%s/%s/issue/%s/page/%03d/pdf?%s",
                checkNotNull(configuration.getHost()),
                checkNotNull(configuration.getWsName()),
                checkNotNull(issueCode),
                checkNotNull(pageNumber),
                getWSParameters());
    }

    String createIssuePDFUrl(final String issueCode) {
        return String.format("%s/%s/issue/%s/pdf?%s",
                checkNotNull(configuration.getHost()),
                checkNotNull(configuration.getWsName()),
                checkNotNull(issueCode),
                getWSParameters());
    }

    String createIssueCoverUrl(final String issueCode, final int resolution) {
        return String.format("%s/%s/issue/%s/cover/%s?%s",
                checkNotNull(configuration.getHost()),
                checkNotNull(configuration.getWsName()),
                checkNotNull(issueCode),
                Integer.toString(resolution),
                getWSParameters());
    }

    String createCatalogIssuesUrl(final String catalog) {
        return String.format("%s/%s/catalog/%s?%s",
                checkNotNull(configuration.getHost()),
                checkNotNull(configuration.getWsName()),
                checkNotNull(catalog),
                getWSParameters());
    }

    String getWSParameters() {
        return String.format("platform=%s&type=%s&device_id=%s",
                checkNotNull(configuration.getPlatform()),
                checkNotNull(configuration.getType()),
                checkNotNull(configuration.getDeviceId()));
    }

    String createLoginUrl(final String catalog, final String issueCode) {
        return String.format("%s/%s/s2s/auth/%s?content=%s",
                checkNotNull(configuration.getHost()),
                checkNotNull(configuration.getVersion()),
                checkNotNull(catalog),
                checkNotNull(issueCode));
    }

    private String httpCall(final String url) throws IOException {
        return getHttpClient().doGet(url);
    }

    private void httpCall(final String url, final OutputStream stream) throws IOException {
        getHttpClient().doGet(url, stream);
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }
}
