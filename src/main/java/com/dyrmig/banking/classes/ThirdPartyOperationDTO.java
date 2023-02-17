package com.dyrmig.banking.classes;

import java.math.BigDecimal;

public class ThirdPartyOperationDTO {
    private BigDecimal amountOfOperation;
    private String targetAccountSecretKey;

    public BigDecimal getAmountOfOperation() {
        return amountOfOperation;
    }

    public void setAmountOfOperation(BigDecimal amountOfOperation) {
        this.amountOfOperation = amountOfOperation;
    }

    public String getTargetAccountSecretKey() {
        return targetAccountSecretKey;
    }

    public void setTargetAccountSecretKey(String targetAccountSecretKey) {
        this.targetAccountSecretKey = targetAccountSecretKey;
    }
}
