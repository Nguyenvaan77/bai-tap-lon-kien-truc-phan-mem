package bankproject.account.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bankproject.account.entity.BankAccount;
import bankproject.account.repository.BankAccountRepository;
import bankproject.account.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Override
    public BankAccount findByAccountNo(long accountNo) {
        Optional<BankAccount> account = bankAccountRepository.findByAccountno(accountNo);
        return account.orElse(null);
    }

    @Override
    public List<BankAccount> findAll() {
        return bankAccountRepository.findAll();
    }

    @Override
    public BankAccount saveAccount(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

    @Override
    public void updateAccount(BankAccount bankAccount) {
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void deleteAccount(BankAccount bankAccount) {
        bankAccountRepository.delete(bankAccount);
    }

    @Override
    public boolean validateAccNo(long accountno) {
        return bankAccountRepository.findByAccountno(accountno).isPresent();
    }

    @Override
    public BankAccount deleteAccount(long accountno) {
        Optional<BankAccount> account = bankAccountRepository.findByAccountno(accountno);
        if (account.isPresent()) {
            bankAccountRepository.delete(account.get());
            return account.get();
        }
        return null;
    }

    @Override
    public List<BankAccount> createAccount(BankAccount newAccount, String userId) {
        newAccount.setUserId(userId);
        bankAccountRepository.save(newAccount);
        return bankAccountRepository.findByUserId(userId);
    }

    @Override
    public List<BankAccount> findByUserId(String userId) {
        return bankAccountRepository.findByUserId(userId);
    }
}
