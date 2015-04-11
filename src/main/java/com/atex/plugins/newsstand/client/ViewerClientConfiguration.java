package com.atex.plugins.newsstand.client;

import com.google.common.base.Objects;

/**
 * Viewer client configuration.
 */
public class ViewerClientConfiguration {

    private String host;
    private String version;
    private String wsName;
    private String platform;
    private String type;
    private String deviceId;
    private int connectionTimeout;
    private int socketTimeout;
    private String proxyHost;
    private int proxyPort;

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getWsName() {
        return wsName;
    }

    public void setWsName(final String wsName) {
        this.wsName = wsName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(final String platform) {
        this.platform = platform;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(final int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(final int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(final String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(final int proxyPort) {
        this.proxyPort = proxyPort;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("host", host)
                      .add("version", version)
                      .add("wsName", wsName)
                      .add("platform", platform)
                      .add("type", type)
                      .add("deviceId", deviceId)
                      .add("connectionTimeout", connectionTimeout)
                      .add("socketTimeout", socketTimeout)
                      .add("proxyHost", proxyHost)
                      .add("proxyPort", proxyPort)
                      .toString();
    }
}
