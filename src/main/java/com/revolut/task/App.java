package com.revolut.task;


import com.google.inject.Guice;
import com.google.inject.servlet.GuiceFilter;
import com.revolut.task.config.GuiceServletModule;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class App {
    public static void main(String[] args) throws Exception {
        Guice.createInjector(new GuiceServletModule());

        Server server = new Server(8080);
        ServletContextHandler servletHandler = new ServletContextHandler();
        servletHandler.addFilter(GuiceFilter.class, "/*", null);
        servletHandler.addServlet(DefaultServlet.class, "/");
        server.setHandler(servletHandler);
        server.start();
        server.join();

    }

}
