package com.digi.common.exception;

public class BARWAHSMEncryptionException extends RuntimeException {
    public BARWAHSMEncryptionException(String code, String message) {
        super(code + ":" + message);
    }
}
