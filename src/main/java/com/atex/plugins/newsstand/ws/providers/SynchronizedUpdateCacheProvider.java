package com.atex.plugins.newsstand.ws.providers;

import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import com.polopoly.application.Application;
import com.polopoly.application.IllegalApplicationStateException;
import com.polopoly.application.servlet.ApplicationServletUtil;
import com.polopoly.cache.SynchronizedUpdateCache;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

/**
 * {@link com.polopoly.cache.SynchronizedUpdateCache} jersey provider.
 */
@Provider
public class SynchronizedUpdateCacheProvider implements InjectableProvider<Context, Type>,
                                                        Injectable<SynchronizedUpdateCache> {

    private static final Logger LOGGER = Logger.getLogger(SynchronizedUpdateCacheProvider.class.getName());

    @Context
    private ServletContext servletContext;

    private SynchronizedUpdateCache updateCache;

    @PostConstruct
    private void geCacheFromServletContext() {
        Application app = ApplicationServletUtil.getApplication(servletContext);
        try {
            updateCache = app.getPreferredApplicationComponent(SynchronizedUpdateCache.class);
        } catch (IllegalApplicationStateException e) {
            LOGGER.log(Level.SEVERE, "Failed to get SynchronizedUpdateCache from application.", e);
        }
    }

    @Override
    public SynchronizedUpdateCache getValue() {
        return updateCache;
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.Singleton;
    }

    @Override
    public Injectable getInjectable(final ComponentContext componentContext, final Context context, final Type type) {
        if (type.equals(SynchronizedUpdateCache.class)) {
            return this;
        } else {
            return null;
        }
    }
}
