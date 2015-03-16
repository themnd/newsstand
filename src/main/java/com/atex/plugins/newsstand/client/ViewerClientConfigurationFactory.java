package com.atex.plugins.newsstand.client;

import static com.google.common.base.Preconditions.checkNotNull;

import com.atex.plugins.newsstand.ConfigurationPolicy;

/**
 * Create a client configuration.
 */
public abstract class ViewerClientConfigurationFactory {

    /**
     * Create the client configuration.
     *
     * @param policy a not null policy.
     * @return a not null configuration.
     */
    public static ViewerClientConfiguration from(final ConfigurationPolicy policy) {
        checkNotNull(policy);
        final ViewerClientConfiguration conf = new ViewerClientConfiguration();
        conf.setHost(policy.getServerHost());
        conf.setVersion(policy.getServerVersion());
        conf.setWsName(policy.getServerWSName());
        conf.setPlatform(policy.getViewerPlatform());
        conf.setType(policy.getViewerType());
        conf.setDeviceId(policy.getViewerDeviceID());
        return conf;
    }
}
