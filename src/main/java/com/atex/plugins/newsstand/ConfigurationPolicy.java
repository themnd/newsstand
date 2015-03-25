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
    private static final String NEWSPAPERS = "newspapers";
    private static final String MAGAZINES = "magazines";
    private static final String COLLATERALS = "collaterals";
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
     * Return a list of collateral codes.
     *
     * @return a not null String.
     */
    public List<String> getCollaterals() {
        return getValueList(COLLATERALS);
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
