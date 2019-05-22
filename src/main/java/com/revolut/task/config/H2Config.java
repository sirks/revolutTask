package com.revolut.task.config;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.h2.jdbcx.JdbcConnectionPool;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Queries;
import org.jooq.impl.DSL;

import static com.revolut.task.DefaultSchema.DEFAULT_SCHEMA;
import static org.jooq.SQLDialect.H2;

public class H2Config implements Provider<Configuration> {
    private final Configuration configuration;

    @Inject
    public H2Config(
            @Named("db.url") String url,
            @Named("db.username") String username,
            @Named("db.password") String password
    ) {
        JdbcConnectionPool connectionPool = JdbcConnectionPool.create(url, username, password);
        connectionPool.setMaxConnections(5);
        DSLContext dslContext = DSL.using(connectionPool, H2);
        createSchema(dslContext);
        this.configuration = dslContext.configuration();
    }

    private void createSchema(DSLContext dslContext) {
        DEFAULT_SCHEMA.tableStream()
                .map(dslContext::ddl)
                .flatMap(Queries::queryStream)
                .forEach(dslContext::execute);
    }

    @Override
    public Configuration get() {
        return configuration;
    }
}
