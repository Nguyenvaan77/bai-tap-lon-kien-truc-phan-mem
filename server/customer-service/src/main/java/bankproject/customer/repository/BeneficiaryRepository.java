package bankproject.customer.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import bankproject.customer.entity.Beneficiaries;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiaries, Integer> {
    List<Beneficiaries> findByUserId(String userId);

    Optional<Beneficiaries> findByBeneficiaryid(int beneficiaryid);
}
