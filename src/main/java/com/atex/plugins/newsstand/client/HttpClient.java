package com.atex.plugins.newsstand.client;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Interface for the http client.
 */
public interface HttpClient {

    /**
     * Do a get of the provided url and return the content as string.
     *
     * @param url the url to fetch.
     * @return the fetched content.
     * @throws IOException if status code != 200
     */
    String doGet(final String url) throws IOException;

    /**
     * Do a get of the provided url and copy the content to the provided output stream.
     *
     * @param url the url to fetch.
     * @param outputStream the fetched content.
     * @return the status code.
     * @throws IOException if status code != 200
     */
    int doGet(final String url, final OutputStream outputStream) throws IOException;

}
