package com.revolut.task.config;

import com.google.inject.Singleton;
import org.jooq.Configuration;

public interface DbConfig {
    Configuration configuration();
}
