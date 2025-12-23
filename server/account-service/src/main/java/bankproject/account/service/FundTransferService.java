package bankproject.account.service;

import bankproject.account.entity.BankAccount;
import bankproject.account.entity.Transactions;

public interface FundTransferService {
    public Transactions save(Transactions transactions);

    public BankAccount updateFundDeducion(BankAccount bankAccount);
}
