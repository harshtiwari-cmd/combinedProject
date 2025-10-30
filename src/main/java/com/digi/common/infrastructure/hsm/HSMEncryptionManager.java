package com.digi.common.infrastructure.hsm;

public interface HSMEncryptionManager {
    String generatePinBlockUnderZPK(String clearPin, String accountNumber, String... purpose);
}

