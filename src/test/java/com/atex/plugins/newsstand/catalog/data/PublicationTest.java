package com.atex.plugins.newsstand.catalog.data;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit test for {@link Publication}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PublicationTest {

    @Test
    public void testGetters() {
        final Publisher pub = new Publisher(RandomStringUtils.random(10), RandomStringUtils.random(10));
        final String id = RandomStringUtils.random(10);
        final String name = RandomStringUtils.random(10);
        final String lang = RandomStringUtils.random(10);

        final Publication mag = new Publication(pub, id, name, lang);
        Assert.assertEquals(pub, mag.getPublisher());
        Assert.assertEquals(id, mag.getId());
        Assert.assertEquals(name, mag.getName());
        Assert.assertEquals(lang, mag.getDefaultLanguage());
        Assert.assertNotNull(mag.getIssues());
    }

    @Test(expected = NullPointerException.class)
    public void testNullIssue() {
        final Publisher pub = new Publisher("id", "name");
        final Publication mag = new Publication(pub, "id", "name", "lang");
        mag.addIssue(null);
    }

    @Test
    public void testAddIssues() {
        final Publisher pub = new Publisher("id", "name");
        final Publication mag = new Publication(pub, "id", "name", "lang");
        mag.addIssue(new Issue());
        mag.addIssue(new Issue());
        mag.addIssue(new Issue());
        Assert.assertEquals(3, mag.getIssues().size());
    }

    @Test(expected = NullPointerException.class)
    public void testNullPublisher() {
        final String id = RandomStringUtils.random(10);
        final String name = RandomStringUtils.random(10);
        final String lang = RandomStringUtils.random(10);

        new Publication(null, id, name, lang);
    }
}