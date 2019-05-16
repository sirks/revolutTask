package com.revolut.task;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.google.inject.servlet.GuiceFilter;
import com.revolut.task.config.GuiceServletModule;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class App {

    public static void main(String[] args) throws Exception {
        Server server = initServer();
        server.start();
        server.join();
    }

    public static Server initServer() {
        Injector guice = Guice.createInjector(new GuiceServletModule());
        Integer port = guice.getInstance(Key.get(Integer.class, Names.named("api.port")));

        Server server = new Server(port);
        ServletContextHandler servletHandler = new ServletContextHandler();
        servletHandler.addFilter(GuiceFilter.class, "/*", null);
        servletHandler.addServlet(DefaultServlet.class, "/");
        server.setHandler(servletHandler);
        return server;
    }

}
