package com.revolut.task.config;

import com.google.inject.name.Names;
import com.revolut.task.api.resources.AccountResource;
import com.revolut.task.api.resources.TransactionResource;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class GuiceServletModule extends JerseyServletModule {

    @Override
    protected void configureServlets() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("config.properties"));
            Names.bindProperties(binder(), props);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        bind(DbConfig.class).to(H2Config.class);

        bind(AccountResource.class);
        bind(TransactionResource.class);
        serve("/*").with(GuiceContainer.class);
    }
}
