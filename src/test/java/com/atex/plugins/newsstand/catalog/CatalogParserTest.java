package com.atex.plugins.newsstand.catalog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.atex.plugins.newsstand.catalog.data.Catalog;
import com.atex.plugins.newsstand.catalog.data.Issue;
import com.atex.plugins.newsstand.catalog.data.Publication;
import com.atex.plugins.newsstand.util.FileUtils;

/**
 * Unit test for {@link com.atex.plugins.newsstand.catalog.CatalogParser}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CatalogParserTest {

    public static final String expectedMD5 = "c5eaad9d652a5c8ca9bddb4eb0d4245f";

    private String uri;

    @Test
    public void testMD5() throws IOException {
        final String md5 = parse("/catalog_web_production.sqlite").md5();
        Assert.assertEquals(expectedMD5, md5);
    }

    @Test
    public void testGetMagazines() throws IOException {
        final List<Publication> publications = parse("/catalog_web_production.sqlite").getPublications();
        Assert.assertNotNull(publications);
        Assert.assertEquals(3, publications.size());
        for (final Publication publication : publications) {
            Assert.assertNotNull(publication);
            Assert.assertEquals(1, publication.getIssues().size());
        }
    }

    @Test
    public void testOpen() throws IOException {
        final Catalog catalog = parse("/catalog_web_production.sqlite").open();
        Assert.assertNotNull(catalog);
        Assert.assertEquals(uri, catalog.getPath());
        Assert.assertEquals(expectedMD5, catalog.getMd5());
        Assert.assertNotNull(catalog.getPublications());
        Assert.assertEquals(3, catalog.getPublications().size());
    }

    @Test
    public void testIssueNotPublished() throws IOException {
        final Catalog catalog = parse("/asvi.sqllite").open();
        final List<Publication> publications = catalog.getPublications();
        Assert.assertEquals(13, publications.size());

        Publication asvi = null;
        for (final Publication p : publications) {
            if (p.getId().equals("ASVI")) {
                asvi = p;
                break;
            }
        }

        Assert.assertNotNull(asvi);
        Assert.assertEquals(558, asvi.getIssues().size());

        Issue issue = null;
        for (final Issue i : asvi.getIssues()) {
            if (i.getIssueCode().equals("ASVI_2015_martedi22dicembretest")) {
                issue = i;
                break;
            }
            Assert.assertTrue(i.getIssueCode(), i.isPublished());
        }
        Assert.assertNull(issue);
    }

    private CatalogParser parse(final String name) throws IOException {
        final InputStream is = this.getClass().getResourceAsStream(name);
        final File tmp = File.createTempFile("catalog.", ".sqlite");
        tmp.deleteOnExit();
        FileUtils.createFile(is, tmp);
        uri = tmp.getAbsolutePath();
        return new CatalogParser(new File(uri));
    }

}