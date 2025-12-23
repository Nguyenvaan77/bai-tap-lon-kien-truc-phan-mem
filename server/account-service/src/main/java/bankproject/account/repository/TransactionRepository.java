package bankproject.account.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bankproject.account.entity.Transactions;

@Repository
public interface TransactionRepository extends JpaRepository<Transactions, Integer> {
    List<Transactions> findByFromAccount(long fromAccount);

    List<Transactions> findByToAccount(long toAccount);
}
