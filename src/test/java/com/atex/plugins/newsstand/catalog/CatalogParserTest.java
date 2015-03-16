package com.atex.plugins.newsstand.catalog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.atex.plugins.newsstand.catalog.data.Catalog;
import com.atex.plugins.newsstand.catalog.data.Magazine;
import com.atex.plugins.newsstand.util.FileUtils;

/**
 * Unit test for {@link com.atex.plugins.newsstand.catalog.CatalogParser}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CatalogParserTest {

    public static final String expectedMD5 = "c5eaad9d652a5c8ca9bddb4eb0d4245f";

    private String uri;
    private CatalogParser parser;

    @Before()
    public void before() throws IOException {
        final InputStream is = this.getClass().getResourceAsStream("/catalog_web_production.sqlite");
        final File tmp = File.createTempFile("catalog.", ".sqlite");
        tmp.deleteOnExit();
        FileUtils.createFile(is, tmp);
        uri = tmp.getAbsolutePath();
        parser = new CatalogParser(new File(uri));
    }

    @Test
    public void testMD5() throws IOException {
        final String md5 = parser.md5();
        Assert.assertEquals(expectedMD5, md5);
    }

    @Test
    public void testGetMagazines() throws IOException {
        final List<Magazine> magazines = parser.getMagazines();
        Assert.assertNotNull(magazines);
        Assert.assertEquals(3, magazines.size());
        for (final Magazine magazine : magazines) {
            Assert.assertNotNull(magazine);
            Assert.assertEquals(1, magazine.getIssues().size());
        }
    }

    @Test
    public void testOpen() throws IOException {
        final Catalog catalog = parser.open();
        Assert.assertNotNull(catalog);
        Assert.assertEquals(uri, catalog.getPath());
        Assert.assertEquals(expectedMD5, catalog.getMd5());
        Assert.assertNotNull(catalog.getMagazines());
        Assert.assertEquals(3, catalog.getMagazines().size());
    }

}