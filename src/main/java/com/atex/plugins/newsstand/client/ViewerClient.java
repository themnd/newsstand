package com.atex.plugins.newsstand.client;

import java.io.IOException;

import com.atex.plugins.newsstand.client.data.AuthData;
import com.atex.plugins.newsstand.catalog.data.Catalog;

/**
 * Interface for the viewer+ client.
 */
public interface ViewerClient {

    Catalog getCatalogIssues(final String catalog) throws IOException;

    AuthData login(final String catalog, final String issueCode) throws IOException;

    byte[] getIssueCover(final String issueCode, final int resolution) throws IOException;

    byte[] getIssuePDFPage(final String issueCode, final int pageNumber) throws IOException;

    byte[] getIssuePDF(final String issueCode) throws IOException;
}
