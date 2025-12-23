package bankproject.authentication.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import bankproject.authentication.entity.User;
import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUserId(String userId);

    User findByResetPasswordToken(String token);

    User findByotp(String otp);

    @Transactional
    @Modifying
    @Query("DELETE FROM User WHERE email = ?1")
    void deleteUser(String email);
}
