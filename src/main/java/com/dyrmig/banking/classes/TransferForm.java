package com.dyrmig.banking.classes;

public class TransferForm {
    private Money amount;
    private Long recipientAccountId;
    private String recipientName;

    public TransferForm() {
    }

    public TransferForm(Money amount, Long recipientAccountId, String recipientName) {
        this.amount = amount;
        this.recipientAccountId = recipientAccountId;
        this.recipientName = recipientName;
    }

    public Money getAmount() {
        return amount;
    }

    public void setAmount(Money amount) {
        this.amount = amount;
    }

    public Long getRecipientAccountId() {
        return recipientAccountId;
    }

    public void setRecipientAccountId(Long recipientAccountId) {
        this.recipientAccountId = recipientAccountId;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
}
