package com.revolut.task;

import com.google.inject.Guice;
import com.revolut.task.config.GuiceConfig;

public class App {
    public static void main(String[] args) {
        System.out.println("Starting App");
        Guice.createInjector(new GuiceConfig());
    }
}
