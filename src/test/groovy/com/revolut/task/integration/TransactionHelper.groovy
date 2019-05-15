package com.revolut.task.integration

import com.revolut.task.api.dto.TransactionDto

class TransactionHelper extends WsHelper {
    final def TRANSACTION_URL = ROOT + "transactions"

    def create(TransactionDto transaction) {
        post(TRANSACTION_URL, transaction, Integer)
    }

    def getBy(Integer id) {
        get(TRANSACTION_URL + "/" + id, TransactionDto)
    }


}
