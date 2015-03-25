package com.atex.plugins.newsstand.client;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.atex.plugins.newsstand.catalog.data.Catalog;
import com.atex.plugins.newsstand.client.data.AuthData;

@RunWith(MockitoJUnitRunner.class)
public class ViewerClientTest {

    @Mock
    private HttpClient httpClient;

    private ViewerClientImpl client;

    private ViewerClientConfiguration config;

    @Before
    public void before() {
        config = new ViewerClientConfiguration();
        config.setHost("H" +RandomStringUtils.randomAlphabetic(5));
        config.setVersion("V" + RandomStringUtils.randomAlphabetic(5));
        config.setWsName("WS" + RandomStringUtils.randomAlphabetic(5));
        config.setPlatform("P" + RandomStringUtils.randomAlphabetic(5));
        config.setType("T" + RandomStringUtils.randomAlphabetic(5));
        config.setDeviceId("D" + RandomStringUtils.randomAlphabetic(5));
        client = Mockito.spy(new ViewerClientImpl(config, httpClient));
    }

    @Test
    public void testLoginOk() throws IOException {
        final String publisher = RandomStringUtils.randomAlphabetic(5);
        final String issueCode = RandomStringUtils.randomAlphanumeric(5);
        final String statusValue = "OK";
        final String urlValue = RandomStringUtils.randomAlphanumeric(50);
        final String returnValue = "{\n" +
                "\"status\":\"" + statusValue + "\", \"url\":\"" + urlValue + "\" }";

        Mockito.when(httpClient.doGet(Mockito.anyString())).thenReturn(returnValue);

        final AuthData auth = client.login(publisher, issueCode);

        Assert.assertNotNull(auth);
        Assert.assertEquals(statusValue, auth.getStatus());
        Assert.assertEquals(urlValue, auth.getUrl());

    }

    @Test
    public void testLoginNotOk() throws IOException {
        final String publisher = RandomStringUtils.randomAlphabetic(5);
        final String issueCode = RandomStringUtils.randomAlphanumeric(5);
        final String statusValue = "NOT_OK";
        final String returnValue = "{\n" +
                "\"status\":\"" + statusValue + "\" }";
        Mockito.when(httpClient.doGet(Mockito.anyString())).thenReturn(returnValue);
        final AuthData auth = client.login(publisher, issueCode);
        Assert.assertNotNull(auth);
        Assert.assertEquals(statusValue, auth.getStatus());
        Assert.assertEquals(null, auth.getUrl());
    }

    @Test
    public void testCatalogParameters() throws IOException {
        final String publisher = RandomStringUtils.randomAlphabetic(5);

        final Catalog expected = new Catalog();
        Mockito.when(httpClient.doGet(Mockito.anyString())).thenReturn("");
        Mockito.doReturn(expected).when(client).parseCatalog(Mockito.any(File.class));

        final Catalog issues = client.getCatalogIssues(publisher);
        Assert.assertNotNull(issues);
        Assert.assertEquals(expected, issues);

        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(httpClient).doGet(captor.capture(), Mockito.any(OutputStream.class));

        final String url = captor.getValue();
        System.out.println(url);
        Assert.assertTrue(url.startsWith(config.getHost()));
        Assert.assertFalse(url.contains(config.getVersion()));
        Assert.assertTrue(url.contains(config.getWsName()));
        Assert.assertTrue(url.contains(config.getPlatform()));
        Assert.assertTrue(url.contains(config.getType()));
        Assert.assertTrue(url.contains(config.getDeviceId()));
    }

    /*
    @Test
    public void reallCallLogin() throws IOException {
        final ViewerClient vc = new ViewerClientImpl(getRealConfig(), new HttpClientImpl());
        final AuthData auth = vc.login("ASAR", "ASAR_2015_test1");
        Assert.assertNotNull(auth);
        System.out.println(auth.toString());

    }

    @Test
    public void reallCallGetCatalogIssues() throws IOException {
        final ViewerClient vc = new ViewerClientImpl(getRealConfig(), new HttpClientImpl());

        final Catalog issues = vc.getCatalogIssues("ASAR");
        Assert.assertNotNull(issues);
        System.out.println(issues.toString());

    }

    @Test
    public void reallCallGetIssueCover() throws IOException {
        final ViewerClient vc = new ViewerClientImpl(getRealConfig(), new HttpClientImpl());

        final Catalog catalog = vc.getCatalogIssues("ASAR");
        Assert.assertNotNull(catalog);

        final Magazine magazine = catalog.getMagazines().get(0);
        final Issue issue = magazine.getIssues().get(0);

        final ViewerClient vc2 = new ViewerClientImpl(getRealConfig(), new HttpClientImpl());
        final byte[] cover = vc2.getIssueCover(issue.getIssueCode(), 600);
        Assert.assertNotNull(cover);

        File f = File.createTempFile("cover.", ".jpg");
        FileUtils.createFile(cover, f);
        System.out.println(f.getAbsolutePath());
        org.apache.commons.io.FileUtils.deleteQuietly(f);

    }
    */

            /*
Esempio di chiamata al catalog dell'Arena:

http://vp-pro12.xorovo.it/ViewerplusWS/catalog/ASAR?platform=web&type=production&device_id=polopoly

Il risultato della chiamata al servizio 'catalog' è un documento SQLite contenente 6 tabelle, delle quali quella fondamentalmente più utile per i vostri scopi è 'issue'.
La tabella 'issue' contiene gli identificativi, le descrizioni e le informazioni di pubblicazione di tutti i numeri.

Queste informazioni sono sufficienti per compilare un catalogo delle testate e dei numeri (i catalog sono ASAR, ASVI, etc).

Per ottenere le copertine, occorre chiamare il servizio 'cover' per ogni issue. Per esempio, la copertina alta 600 pixel  del numero test1 dell'Arena si ottiene con:
http://vp-pro12.xorovo.it/ViewerplusWS/issue/ASAR_2015_test1/cover/600?platform=web&device_id=polopoly

dove il valore del parametro 'issueCode’ (ASAR_2015_test1) è reperibile nella colonna 'issue_code' della tabella 'issue’.

Server: http://vp-pro12.xorovo.it
Versione dello sfogliatore a cui collegarsi: vpweb25/
Issue di test: http://vp-pro12.xorovo.it/vpweb25/magazines/ASAR/issues/ASAR_2015_test1/index.html?ticket=3ba8a73a6fb8ffe60f0c48a8b06d0f9f2f0e4cab

I magazines sono identificati sui nostri server con questi codici:

Arena: ASAR
BresciaOggi: ASBS
Giornale di Vicenza: ASVI


Codici issue di test:

Arena: ASAR_2015_test1
BresciaOggi: ASBS_2015_test1
Giornale di Vicenza: ASVI_2015_test1


         */

    private ViewerClientConfiguration getRealConfig() {
        ViewerClientConfiguration config = new ViewerClientConfiguration();
        config.setHost("http://vp-pro12.xorovo.it");
        config.setVersion("vpweb25");
        config.setWsName("ViewerplusWS");
        config.setPlatform("web");
        config.setType("production");
        config.setDeviceId("polopoly");
        return config;
    }

}