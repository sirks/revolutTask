package com.revolut.task.config;

import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Logger;

public class H2Config {
    private static final Logger LOG = Logger.getLogger("H2Config");
    private static final String CONNECTION = "jdbc:h2:mem:";

    private static JdbcConnectionPool connectionPool = connectionPool();

    private static JdbcConnectionPool connectionPool() {
        JdbcConnectionPool pool = JdbcConnectionPool.create(CONNECTION, "", "");
        pool.setMaxConnections(5);
        return pool;
    }

    public static Optional<Connection> connection() {
        try {
            return Optional.of(connectionPool.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static void main(String[] args) {
        Optional<Connection> connection = connection();
        connection.ifPresent(c -> {
            try {
                LOG.info("closing connection");
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        connectionPool.dispose();
    }
}
