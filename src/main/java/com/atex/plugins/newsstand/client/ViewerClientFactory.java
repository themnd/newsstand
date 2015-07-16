package com.atex.plugins.newsstand.client;

/**
 * Factory for viewerClient.
 */
public final class ViewerClientFactory {

    private static ViewerClientFactory instance;

    private final ViewerClientConfiguration configuration;
    private final ClientFactory clientFactory;
    private final HttpClientFactory httpClientFactory;

    private ViewerClientFactory(final ViewerClientConfiguration configuration,
                                final ClientFactory clientFactory) {
        this.configuration = configuration;
        this.clientFactory = clientFactory;
        final ViewerClientFactory self  = this;
        this.httpClientFactory = new HttpClientFactory() {

            @Override
            public HttpClient createHttpClient() {
                return self.createHttpClient();
            }

        };
    }

    private ViewerClientFactory(final ViewerClientConfiguration configuration,
                                final ClientFactory clientFactory,
                                final HttpClientFactory httpClientFactory) {
        this.configuration = configuration;
        this.clientFactory = clientFactory;
        this.httpClientFactory = httpClientFactory;
    }

    public ViewerClient createClient() {
        return clientFactory.createViewerClient(configuration, httpClientFactory.createHttpClient());
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
        instance = new ViewerClientFactory(configuration, new ClientFactory() {
            @Override
            public ViewerClient createViewerClient(final ViewerClientConfiguration configuration,
                                                   final HttpClient httpClient) {
                return new ViewerClientImpl(configuration, httpClient);
            }
        });
    }

    public static void initInstance(final ViewerClientConfiguration configuration,
                                    final ClientFactory clientFactory,
                                    final HttpClientFactory httpClientFactory) {
        instance = new ViewerClientFactory(configuration, clientFactory, httpClientFactory);
    }

    public static ViewerClientFactory getInstance() {
        return instance;
    }

    public interface ClientFactory {
        ViewerClient createViewerClient(final ViewerClientConfiguration configuration,
                                        final HttpClient httpClient);
    }

    public interface HttpClientFactory {
        HttpClient createHttpClient();
    }

}
