package com.atex.plugins.newsstand;

import java.util.List;

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

    public static final String CONFIG_EXTERNAL_ID = "plugins.com.atex.plugins.newsstand.Config";

    private static final String HOST = "host";
    private static final String VERSION = "version";
    private static final String WSNAME = "wsname";
    private static final String CATALOGS = "catalogs";
    private static final String PLATFORM = "platform";
    private static final String TYPE = "type";
    private static final String DEVICE_ID = "device_id";

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
        final String value = Strings.nullToEmpty(getChildValue(CATALOGS, ""));
        return Lists.newArrayList(
                Splitter
                        .on(",")
                        .omitEmptyStrings()
                        .trimResults()
                        .split(value));
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

}
