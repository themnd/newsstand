package com.atex.plugins.newsstand.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.google.common.base.Strings;

/**
 * Implementation for {@link com.atex.plugins.newsstand.client.HttpClient}.
 */
public class HttpClientImpl implements HttpClient {

    private static final Logger LOGGER = Logger.getLogger(HttpClientImpl.class.getName());

    private int connectionTimeout = 5 * 1000;
    private int socketTimeout = 30 * 1000;
    private String proxyHost = null;
    private int proxyPort = 8080;


    private transient org.apache.http.client.HttpClient httpclient;

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(final int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(final int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(final int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(final String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public HttpClientImpl() {
        httpclient = new DefaultHttpClient(createHttpParams());
    }

    @Override
    public String doGet(final String url) throws IOException {
        try (final ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            callService(url, stream);
            return stream.toString();
        }
    }

    @Override
    public int doGet(final String url, final OutputStream outputStream) throws IOException {
        return callService(url, outputStream);
    }

    private int callService(final String url, final OutputStream outputStream) throws IOException
    {
        LOGGER.info("calling url " + url);

        HttpEntity responseEntity = null;

        try {

            final HttpGet httpGet = new HttpGet(url);
            httpGet.setParams(createHttpParams());

            final HttpResponse response = getHttpclient().execute(httpGet);
            responseEntity = response.getEntity();

            final int statusCode;
            try (final InputStream in = responseEntity.getContent()) {

                statusCode = response.getStatusLine().getStatusCode();

                LOGGER.info("status code " + url + " (" + statusCode + ")");

                if (HttpStatus.SC_OK == statusCode) {

                    try {
                        final byte[] content = EntityUtils.toByteArray(responseEntity);
                        IOUtils.write(content, outputStream);
                    } finally {
                        IOUtils.closeQuietly(outputStream);
                    }

                    return statusCode;
                }
            }

            throw new IOException("Http status: " + statusCode);

        } finally {
            EntityUtils.consumeQuietly(responseEntity);

            // release connection
            if (httpclient != null) {
                final ClientConnectionManager connectionManager = httpclient.getConnectionManager();
                connectionManager.shutdown();
            }
        }

    }

    private HttpParams createHttpParams()
    {
        final HttpParams httpParams = new BasicHttpParams();

        LOGGER.fine("connectionTimeout: " + connectionTimeout + " - socketTimeout: " + socketTimeout);

        // set http connection timeout
        HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeout);

        // set http socket timeout
        HttpConnectionParams.setSoTimeout(httpParams, socketTimeout);

        if (!Strings.isNullOrEmpty(proxyHost)) {

            LOGGER.fine("Setting proxy for this client " + proxyHost + ":" + proxyPort);

            final HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
        }

        return httpParams;
    }

    public org.apache.http.client.HttpClient getHttpclient() {
        return httpclient;
    }
}
