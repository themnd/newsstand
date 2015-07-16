package com.atex.plugins.newsstand.controller;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.atex.plugins.newsstand.catalog.CatalogParser;
import com.atex.plugins.newsstand.catalog.data.Catalog;
import com.atex.plugins.newsstand.client.HttpClient;
import com.atex.plugins.newsstand.client.ViewerClient;
import com.atex.plugins.newsstand.client.ViewerClientConfiguration;
import com.atex.plugins.newsstand.client.ViewerClientFactory;
import com.atex.plugins.newsstand.client.ViewerClientFactory.ClientFactory;
import com.atex.plugins.newsstand.client.ViewerClientFactory.HttpClientFactory;
import com.atex.plugins.newsstand.util.CatalogUtil;
import com.atex.plugins.newsstand.util.FileUtils;

/**
 * Unit test for {@link NewsstandRenderController}.
 *
 * @author mnova
 */
@RunWith(MockitoJUnitRunner.class)
public class NewsstandRenderControllerTest {

    @Mock
    ViewerClient viewerClient;

    @Mock
    HttpClient httpClient;

    @Mock
    ViewerClientConfiguration configuration;

    @Before
    public void before() {
        ViewerClientFactory.initInstance(configuration, new ClientFactory() {
            @Override
            public ViewerClient createViewerClient(final ViewerClientConfiguration configuration, final HttpClient httpClient) {
                return viewerClient;
            }
        }, new HttpClientFactory() {
            @Override
            public HttpClient createHttpClient() {
                return httpClient;
            }
        });
        Assert.assertNotNull(ViewerClientFactory.getInstance());
        CatalogUtil.getInstance().clear();
    }

    @Test
    public void testCatalogWithoutCache() throws IOException {
        final Catalog cat1 = loadCatalog("/catalog_web_production.sqlite");
        Mockito.when(viewerClient.getCatalogIssues(Mockito.eq("ASAR"))).thenReturn(cat1);
        //final Catalog cat2 = loadCatalog("/catalog_web_updated.sqlite");
        final Catalog cat2 = CatalogUtil.getInstance().getCatalog("ASAR");
        Assert.assertNull(cat2);
    }

    @Test
    public void testCatalogWithCache() throws IOException {
        final Catalog cat1 = loadCatalog("/catalog_web_production.sqlite");
        Mockito.when(viewerClient.getCatalogIssues(Mockito.eq("ASAR"))).thenReturn(cat1);
        loadCatalogInCache("ASAR");
        //final Catalog cat2 = loadCatalog("/catalog_web_updated.sqlite");
        final Catalog cat2 = CatalogUtil.getInstance().getCatalog("ASAR");
        Assert.assertNotNull(cat2);
        Assert.assertEquals(cat1, cat2);
    }

    @Test
    public void testCatalogUpdate() throws IOException {
        final Catalog cat1 = loadCatalog("/catalog_web_production.sqlite");
        final Catalog cat2 = loadCatalog("/catalog_web_updated.sqlite");
        Mockito.when(viewerClient.getCatalogIssues(Mockito.eq("ASAR"))).thenReturn(cat1).thenReturn(cat2);

        loadCatalogInCache("ASAR");
        final Catalog cat3 = CatalogUtil.getInstance().getCatalog("ASAR");
        Assert.assertNotNull(cat3);
        Assert.assertEquals(cat1, cat3);

        loadCatalogInCache("ASAR");
        final Catalog cat4 = CatalogUtil.getInstance().getCatalog("ASAR");
        Assert.assertNotNull(cat4);
        Assert.assertEquals(cat2, cat4);
    }

    private void loadCatalogInCache(final String name) {
        NewsstandRenderController.getCatalogFromCache(name, true);
    }

    private Catalog loadCatalog(final String name) throws IOException {
        final InputStream is = this.getClass().getResourceAsStream(name);
        final File tmp = File.createTempFile("catalog.", ".sqlite");
        tmp.deleteOnExit();
        FileUtils.createFile(is, tmp);
        final CatalogParser parser = new CatalogParser(tmp);
        final Catalog catalog = parser.open();
        tmp.delete();
        return catalog;
    }


}