package bankproject.account.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bankproject.account.entity.LoanAccount;

@Repository
public interface LoanRepository extends JpaRepository<LoanAccount, Long> {
    Optional<LoanAccount> findByLoanaccountno(Long loanaccountno);
}
