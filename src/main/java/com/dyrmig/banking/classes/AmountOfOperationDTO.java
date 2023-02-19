package com.dyrmig.banking.classes;

import java.math.BigDecimal;

public class AmountOfOperationDTO {
    private BigDecimal amountOfOperation;

    public AmountOfOperationDTO() {
    }
    public AmountOfOperationDTO(BigDecimal amountOfOperation) {
        this.amountOfOperation = amountOfOperation;
    }
    public BigDecimal getAmountOfOperation() {
        return amountOfOperation;
    }

    public void setAmountOfOperation(BigDecimal amountOfOperation) {
        this.amountOfOperation = amountOfOperation;
    }
}
