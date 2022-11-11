package com.ajcp.junit.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class Bank {

    private String name;
    private List<Account> accounts;

    public Bank() {
        accounts = new ArrayList<>();
    }

    public void transfer(Account origin, Account destiny, BigDecimal amount) {
        origin.debit(amount);
        destiny.credit(amount);
    }

    public void addAccount(Account account) {
        accounts.add(account);
        account.setBank(this);
    }
}
