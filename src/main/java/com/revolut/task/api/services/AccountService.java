package com.revolut.task.api.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.revolut.task.api.dto.AccountDto;
import com.revolut.task.config.H2Config;
import com.revolut.task.tables.daos.AccountDao;
import com.revolut.task.tables.pojos.Account;

import java.util.Optional;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.math.BigDecimal.ZERO;

@Singleton
public class AccountService {
    private final static Logger LOG = Logger.getLogger("AccountService");

    private final AccountDao accountDao;
    private final IbanService ibanService;

    @Inject
    public AccountService(H2Config h2Config, AccountDao accountDao, IbanService ibanService) {
        this.accountDao = accountDao;
        this.ibanService = ibanService;
        this.accountDao.setConfiguration(h2Config.configuration);
    }

    public Optional<AccountDto> getById(Integer accountId) {
        return Optional.ofNullable(accountDao.fetchOneById(accountId)).map(AccountDto::new);
    }

    public Integer create(AccountDto account) {
        validate(account);
        Account pojo = account.pojo();
        pojo.setBalance(ZERO);
        pojo.setId(null);
        accountDao.insert(pojo);
        return pojo.getId();
    }

    private void validate(AccountDto account) {
        if (!ibanService.isValid(account.iban)) {
            String message = format("Incorrect iban length %s", account.iban);
            LOG.warning(message);
//            TODO should intercept and serialize the exception
            throw new RuntimeException(message);
        }
        //TODO other validations
    }
}
