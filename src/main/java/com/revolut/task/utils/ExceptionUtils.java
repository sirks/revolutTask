package com.revolut.task.utils;

import java.util.logging.Logger;

//TODO build some serializable exeptions
public class ExceptionUtils {
    public static RuntimeException buildException(Logger log, String message) {
        log.warning(message);
        return new RuntimeException(message);
    }
}
