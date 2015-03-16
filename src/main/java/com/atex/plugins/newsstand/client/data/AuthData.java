package com.atex.plugins.newsstand.client.data;

import com.google.common.base.Objects;

/**
 * Authorization data.
 */
public class AuthData {
    private String status;
    private String url;

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("status", status)
                      .add("url", url)
                      .toString();
    }

}
