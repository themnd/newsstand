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
        final HttpClient httpClient = new HttpClientImpl();
        return new ViewerClientImpl(configuration, httpClient);
    }

    public static void initInstance(final ViewerClientConfiguration configuration) {
        instance = new ViewerClientFactory(configuration);
    }

    public static ViewerClientFactory getInstance() {
        return instance;
    }

}
