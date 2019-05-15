package com.revolut.task.integration

import com.revolut.task.App
import org.eclipse.jetty.server.Server

class MockServer {

    static final int PORT = 9000
    static final String HOST = "http://localhost"

    static Server server

    static def start() {
        if (server) {
            return
        }
        server = App.initServer(PORT)
    }

    static def stop() {
        if (server) {
            server.stop()
        }
        server = null
    }
}
