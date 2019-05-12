package com.revolut.task.config;

import com.google.inject.AbstractModule;

import java.sql.Driver;

//import java.util.logging.Logger;

public class GuiceConfig extends AbstractModule {
    @Override
    protected void configure() {
//        bind(Driver.class).to(org.h2.Driver.class);
    }
}