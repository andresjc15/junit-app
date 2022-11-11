package com.ajcp.junit.app.model;

import com.ajcp.junit.app.exception.InsufficientAmountException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    private String person;
    private BigDecimal balance;

    private Bank bank;

    public Account(String person, BigDecimal balance) {
        this.person = person;
        this.balance = balance;
    }

    public void debit(BigDecimal amount) {
        BigDecimal newBalance = this.balance.subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientAmountException("Insufficient amount");
        }
        this.balance = newBalance;
    }

    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }

}
