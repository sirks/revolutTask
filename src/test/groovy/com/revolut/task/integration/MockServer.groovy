package com.revolut.task.integration

import com.revolut.task.App
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector

class MockServer {

    static final String HOST = "http://localhost"
    static final int PORT = 9000

    static Server server

    static def start() {
        if (server) {
            return
        }
        server = App.initServer()
        ((ServerConnector) server.connectors[0]).setPort(PORT)
        server.start()
    }
}
