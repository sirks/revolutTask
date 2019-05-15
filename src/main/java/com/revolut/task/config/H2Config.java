package com.revolut.task.config;

import com.google.inject.Singleton;
import org.h2.jdbcx.JdbcConnectionPool;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Queries;
import org.jooq.impl.DSL;

import static com.revolut.task.DefaultSchema.DEFAULT_SCHEMA;
import static org.jooq.SQLDialect.H2;

@Singleton
public class H2Config implements DbConfig{
    private static final String URL = "jdbc:h2:mem:";
    private final Configuration configuration;

    public H2Config() {
        System.out.println("building dbConfig");
        JdbcConnectionPool connectionPool = JdbcConnectionPool.create(URL, "", "");
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
    public Configuration configuration() {
        return configuration;
    }
}
