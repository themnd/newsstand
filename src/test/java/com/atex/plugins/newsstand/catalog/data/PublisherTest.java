package com.atex.plugins.newsstand.catalog.data;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit test for {@link com.atex.plugins.newsstand.catalog.data.Publisher}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PublisherTest {

    @Test
    public void testGetters() {
        final String id = RandomStringUtils.random(10);
        final String name = RandomStringUtils.random(10);

        final Publisher pub = new Publisher(id, name);
        Assert.assertEquals(id, pub.getId());
        Assert.assertEquals(name, pub.getName());
    }

}