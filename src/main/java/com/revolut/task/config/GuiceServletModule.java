package com.revolut.task.config;

import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.revolut.task.api.helpers.TransactionLock;
import com.revolut.task.api.helpers.TransactionRetryLock;
import com.revolut.task.api.repository.Accounts;
import com.revolut.task.api.repository.AccountsDao;
import com.revolut.task.api.repository.Transactions;
import com.revolut.task.api.repository.TransactionsDao;
import com.revolut.task.api.resources.AccountResource;
import com.revolut.task.api.resources.TransactionResource;
import com.revolut.task.api.services.AccountService;
import com.revolut.task.api.services.SimpleAccountService;
import com.revolut.task.api.services.SimpleTransactionService;
import com.revolut.task.api.services.TransactionService;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;
import org.jooq.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class GuiceServletModule extends JerseyServletModule {

    @Override
    protected void configureServlets() {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("config.properties"));
            Names.bindProperties(binder(), props);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        bind(Configuration.class).toProvider(H2Config.class).in(Singleton.class);

        bind(AccountService.class).to(SimpleAccountService.class);
        bind(TransactionService.class).to(SimpleTransactionService.class);
        bind(TransactionLock.class).to(TransactionRetryLock.class);

        bind(Accounts.class).to(AccountsDao.class);
        bind(Transactions.class).to(TransactionsDao.class);

        bind(AccountResource.class);
        bind(TransactionResource.class);
        serve("/*").with(GuiceContainer.class);
    }
}
