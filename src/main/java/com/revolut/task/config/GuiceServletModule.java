package com.revolut.task.config;

import com.revolut.task.api.resources.AccountResource;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class GuiceServletModule extends JerseyServletModule {

    @Override
    protected void configureServlets() {
        bind(AccountResource.class);
//            Map<String, String> params = new HashMap<>();
//            params.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
//            serve("/*").with(GuiceContainer.class, params);
        serve("/*").with(GuiceContainer.class);
    }
}
