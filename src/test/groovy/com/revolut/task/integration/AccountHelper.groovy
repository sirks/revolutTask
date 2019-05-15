package com.revolut.task.integration

import com.revolut.task.api.dto.AccountDto

class AccountHelper extends WsHelper {
    static long accountNumber = 1234567890

    synchronized static String nextAccountNumber() {
        return (accountNumber++).toString()
    }

    final def ACCOUNT_URL = ROOT + "accounts"

    def create(AccountDto account) {
        post(ACCOUNT_URL, account, Integer)
    }

    def getBy(Integer id) {
        get(ACCOUNT_URL + "/" + id, AccountDto)
    }

}
