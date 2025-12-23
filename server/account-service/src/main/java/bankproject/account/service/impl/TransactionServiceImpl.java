package bankproject.account.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bankproject.account.entity.BankAccount;
import bankproject.account.entity.Transactions;
import bankproject.account.repository.BankAccountRepository;
import bankproject.account.repository.TransactionRepository;
import bankproject.account.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Override
    public List<Transactions> getDetailsByAccount(long fromAccount) {
        return transactionRepository.findByFromAccount(fromAccount);
    }

    @Override
    public List<Transactions> getTransactionsByReceiver(long toAccount) {
        return transactionRepository.findByToAccount(toAccount);
    }

    @Override
    public Transactions save(Transactions theTransactions) {
        return transactionRepository.save(theTransactions);
    }

    @Override
    public Transactions setTransactions(BankAccount bankAccount, long toAccount, double amount, String description,
            String status) {
        Transactions transaction = new Transactions();
        transaction.setFromAccount(bankAccount.getAccountno());
        transaction.setToAccount(toAccount);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setTransactionStatus(status);
        transaction.setSenderBal(bankAccount.getBalance());
        transaction.setTransactionDate(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        transaction.setTransactionTime(new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()));
        return transaction;
    }

    @Override
    public Transactions getTransactionsById(int transactionId) {
        Optional<Transactions> transaction = transactionRepository.findById(transactionId);
        return transaction.orElse(null);
    }

    @Override
    public List<Transactions> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    public List<Transactions> getAllByAccount(long accountno) {
        List<Transactions> outgoing = transactionRepository.findByFromAccount(accountno);
        List<Transactions> incoming = transactionRepository.findByToAccount(accountno);
        outgoing.addAll(incoming);
        return outgoing;
    }

    @Override
    public Transactions getCurrentTransaction(long accountno) {
        List<Transactions> transactions = getAllByAccount(accountno);
        if (transactions.size() > 0) {
            return transactions.get(transactions.size() - 1);
        }
        return null;
    }
}
