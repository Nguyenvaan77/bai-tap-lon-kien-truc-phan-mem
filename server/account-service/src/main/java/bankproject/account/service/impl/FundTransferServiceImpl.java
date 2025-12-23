package bankproject.account.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bankproject.account.entity.BankAccount;
import bankproject.account.entity.Transactions;
import bankproject.account.repository.BankAccountRepository;
import bankproject.account.repository.TransactionRepository;
import bankproject.account.service.FundTransferService;

@Service
public class FundTransferServiceImpl implements FundTransferService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Override
    public Transactions save(Transactions transactions) {
        return transactionRepository.save(transactions);
    }

    @Override
    public BankAccount updateFundDeducion(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }
}
