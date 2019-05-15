package com.revolut.task.config;

import com.revolut.task.api.resources.AccountResource;
import com.revolut.task.api.resources.TransactionResource;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

public class GuiceServletModule extends JerseyServletModule {

    @Override
    protected void configureServlets() {
        bind(DbConfig.class).to(H2Config.class);

        bind(AccountResource.class);
        bind(TransactionResource.class);
        serve("/*").with(GuiceContainer.class);
    }
}
