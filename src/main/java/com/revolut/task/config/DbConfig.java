package com.revolut.task.config;

import com.google.inject.Singleton;
import org.jooq.Configuration;

@Singleton
public interface DbConfig {
    Configuration configuration();
}
