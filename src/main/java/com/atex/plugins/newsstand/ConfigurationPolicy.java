package com.atex.plugins.newsstand;

import java.util.List;
import java.util.logging.Logger;

import com.atex.plugins.baseline.policy.BaselinePolicy;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.polopoly.model.DescribesModelType;

/**
 * Policy for the plugin configuration.
 */
@DescribesModelType
public class ConfigurationPolicy extends BaselinePolicy {

    private static final Logger LOGGER = Logger.getLogger(ConfigurationPolicy.class.getName());

    public static final String CONFIG_EXTERNAL_ID = "plugins.com.atex.plugins.newsstand.Config";

    private static final String HOST = "host";
    private static final String VERSION = "version";
    private static final String WSNAME = "wsname";
    private static final String CATALOGS = "catalogs";
    private static final String NEWSPAPERS = "newspapers";
    private static final String MAGAZINES = "magazines";
    private static final String SEASONALS = "seasonals";
    private static final String SPECIALS = "specials";
    private static final String PLATFORM = "platform";
    private static final String TYPE = "type";
    private static final String DEVICE_ID = "device_id";
    private static final String CONNECTIONTIMEOUT = "connectionTimeout";
    private static final String SOCKETTIMEOUT = "socketTimeout";
    private static final String PROXYHOST = "proxyHost";
    private static final String PROXYPORT = "proxyPort";

    private static final int DEFAULT_CONNECTION_TIMEOUT = 5 * 1000;
    private static final int DEFAULT_SOCKET_TIMEOUT = 30 * 1000;
    private static final int DEFAULT_PROXY_PORT = 8080;

    /**
     * Return the server host name.
     *
     * @return a not null String.
     */
    public String getServerHost() {
        return Strings.nullToEmpty(getChildValue(HOST, ""));
    }

    /**
     * Return the server version.
     *
     * @return a not null String.
     */
    public String getServerVersion() {
        return Strings.nullToEmpty(getChildValue(VERSION, ""));
    }

    /**
     * Return the server web service name.
     *
     * @return a not null String.
     */
    public String getServerWSName() {
        return Strings.nullToEmpty(getChildValue(WSNAME, ""));
    }

    /**
     * Return a list of catalogs.
     *
     * @return a not null String.
     */
    public List<String> getCatalogs() {
        return getValueList(CATALOGS);
    }

    /**
     * Return a list of newspaper codes.
     *
     * @return a not null String.
     */
    public List<String> getNewspapers() {
        return getValueList(NEWSPAPERS);
    }

    /**
     * Return a list of magazine codes.
     *
     * @return a not null String.
     */
    public List<String> getMagazines() {
        return getValueList(MAGAZINES);
    }

    /**
     * Return a list of seasonals codes.
     *
     * @return a not null String.
     */
    public List<String> getSeasonals() {
        return getValueList(SEASONALS);
    }

    /**
     * Return a list of specials codes.
     *
     * @return a not null String.
     */
    public List<String> getSpecials() {
        return getValueList(SPECIALS);
    }

    /**
     * Return the viewer platform.
     *
     * @return a not null String.
     */
    public String getViewerPlatform() {
        return Strings.nullToEmpty(getChildValue(PLATFORM, ""));
    }

    /**
     * Return the viewer type.
     *
     * @return a not null String.
     */
    public String getViewerType() {
        return Strings.nullToEmpty(getChildValue(TYPE, ""));
    }

    /**
     * Return the viewer device ID.
     *
     * @return a not null String.
     */
    public String getViewerDeviceID() {
        return Strings.nullToEmpty(getChildValue(DEVICE_ID, ""));
    }

    /**
     * Return the connection timeout for the http client.
     *
     * @return a valid integer.
     */
    public int getConnectionTimeout() {
        return getChildIntValue(CONNECTIONTIMEOUT, DEFAULT_CONNECTION_TIMEOUT);
    }

    /**
     * Return the socket timeout for the http client.
     *
     * @return a valid integer.
     */
    public int getSocketTimeout() {
        return getChildIntValue(SOCKETTIMEOUT, DEFAULT_SOCKET_TIMEOUT);
    }

    /**
     * Return the proxy host for the http client.
     *
     * @return a not null String.
     */
    public String getProxyHost() {
        return Strings.nullToEmpty(getChildValue(PROXYHOST, ""));
    }

    /**
     * Return the proxy port for the http client.
     *
     * @return a valid integer.
     */
    public int getProxyPort() {
        return getChildIntValue(PROXYPORT, DEFAULT_PROXY_PORT);
    }

    private int getChildIntValue(final String name, final int defaultValue) {
        final String value = getChildValue(name, null);
        if (!Strings.isNullOrEmpty(value)) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                LOGGER.severe("cannot parse a valid value for " + name + ": " + e.getMessage());
            }
        }
        return defaultValue;
    }

    private List<String> getValueList(final String name) {
        final String value = Strings.nullToEmpty(getChildValue(name, ""));
        return Lists.newArrayList(
                Splitter
                        .on(",")
                        .omitEmptyStrings()
                        .trimResults()
                        .split(value));
    }

}
