package com.atex.plugins.newsstand.client;

/**
 * Factory for viewerClient.
 */
public final class ViewerClientFactory {

    private static ViewerClientFactory instance;

    private final ViewerClientConfiguration configuration;

    private ViewerClientFactory(final ViewerClientConfiguration configuration) {
        this.configuration = configuration;
    }

    public ViewerClient createClient() {
        return new ViewerClientImpl(configuration, createHttpClient());
    }

    private HttpClient createHttpClient() {
        final HttpClientImpl httpClient = new HttpClientImpl();
        httpClient.setConnectionTimeout(configuration.getConnectionTimeout());
        httpClient.setSocketTimeout(configuration.getSocketTimeout());
        httpClient.setProxyHost(configuration.getProxyHost());
        httpClient.setProxyPort(configuration.getProxyPort());
        return httpClient;
    }

    public static void initInstance(final ViewerClientConfiguration configuration) {
        instance = new ViewerClientFactory(configuration);
    }

    public static ViewerClientFactory getInstance() {
        return instance;
    }

}
