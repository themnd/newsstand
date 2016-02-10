package com.atex.plugins.newsstand.client;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit test for {@link ViewerClientImpl}
 *
 * @author mnova
 */
@RunWith(MockitoJUnitRunner.class)
public class ViewerClientImplTest {

    @Mock
    ViewerClientConfiguration configuration;

    @Mock
    HttpClient httpClient;

    @Test
    public void testCreateIssuePDFUrl() {

        final String issueCode = RandomStringUtils.randomAlphabetic(10);

        final ViewerClientImpl client = createClient();
        final String url = client.createIssuePDFUrl(issueCode);

        assertStandardUrl(url, issueCode);
        Assert.assertTrue(url, url.contains("/pdf?"));
    }

    @Test
    public void testCreateIssuePDFPageUrl() {

        final String issueCode = RandomStringUtils.randomAlphabetic(10);
        final int page = 4;

        final ViewerClientImpl client = createClient();
        final String url = client.createIssuePDFUrl(issueCode, page);

        assertStandardUrl(url, issueCode);
        Assert.assertTrue(url, url.contains("/page/004/pdf?"));
    }

    private void assertStandardUrl(final String url, final String issueCode) {
        Assert.assertNotNull(url);
        Assert.assertTrue(url, url.startsWith("http://vp-pro12.xorovo.it"));
        Assert.assertTrue(url, url.contains("/ViewerplusWS/"));
        Assert.assertTrue(url, url.contains("/" + issueCode + "/"));
        Assert.assertTrue(url, url.endsWith("?platform=web&type=production&device_id=polopoly"));
    }

    private ViewerClientImpl createClient() {
        Mockito.when(configuration.getHost()).thenReturn("http://vp-pro12.xorovo.it");
        Mockito.when(configuration.getWsName()).thenReturn("ViewerplusWS");
        Mockito.when(configuration.getPlatform()).thenReturn("web");
        Mockito.when(configuration.getType()).thenReturn("production");
        Mockito.when(configuration.getDeviceId()).thenReturn("polopoly");

        final ViewerClientImpl client = new ViewerClientImpl(configuration, httpClient);
        return client;
    }

}