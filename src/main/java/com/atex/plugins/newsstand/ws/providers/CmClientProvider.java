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
import com.polopoly.cm.client.CmClient;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

/**
 * {@link com.polopoly.cm.client.CmClient} jersey provider.
 */
@Provider
public class CmClientProvider implements InjectableProvider<Context, Type>,
                                         Injectable<CmClient> {

    private static final Logger LOGGER = Logger.getLogger(CmClientProvider.class.getName());

    @Context
    private ServletContext servletContext;

    private CmClient cmClient;

    @PostConstruct
    private void getCmClientFromServletContext() {
        Application app = ApplicationServletUtil.getApplication(servletContext);
        try {
            cmClient = app.getPreferredApplicationComponent(CmClient.class);
        } catch (IllegalApplicationStateException exception) {
            LOGGER.log(Level.SEVERE, "Failed to get PolicyCMServer from application.", exception);
            throw new RuntimeException(exception);
        }
    }

    @Override
    public CmClient getValue() {
        return cmClient;
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.Singleton;
    }

    @Override
    public Injectable getInjectable(final ComponentContext ic, final Context context, final Type type) {
        if (type.equals(CmClient.class)) {
            return this;
        } else {
            return null;
        }
    }

}
