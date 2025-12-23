package bankproject.authentication.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import bankproject.authentication.dto.ChangePasswordReq;
import bankproject.authentication.entity.User;
import bankproject.authentication.repository.UserRepository;
import bankproject.authentication.service.SignUpService;
import java.util.List;
import java.util.Optional;

@Service
public class SignUpServiceImpl implements SignUpService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    PasswordEncoder passwordEncoded;

    @Override
    public User createUser(User user) {
        user.setPassword(passwordEncoded.encode(user.getPassword()));
        User createdUser = userRepo.save(user);
        return createdUser;
    }

    @Override
    public Optional<User> getAUser(String userId) {
        Optional<User> user = userRepo.findById(userId);
        return user;
    }

    @Override
    public List<User> GetAllUsers() {
        List<User> users = userRepo.findAll();
        return users;
    }

    @Override
    public boolean checkEmail(String email) {
        return userRepo.findByEmail(email).isPresent();
    }

    @Override
    public User findByResetPasswordToken(String token) {
        return userRepo.findByResetPasswordToken(token);
    }

    @Override
    public void updateResetPasswordToken(String token, String email) {
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isPresent()) {
            User theUser = userOpt.get();
            theUser.setResetPasswordToken(token);
            userRepo.save(theUser);
        }
    }

    @Override
    public void updatePassword(String password, String token) {
        User theUser = userRepo.findByResetPasswordToken(token);
        if (theUser != null) {
            theUser.setPassword(passwordEncoded.encode(password));
            theUser.setResetPasswordToken(null);
            userRepo.save(theUser);
        }
    }

    @Override
    public User findByOTP(String otp) {
        return userRepo.findByotp(otp);
    }

    @Override
    public void deleteAccount(String email) {
        userRepo.deleteUser(email);
    }

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }

    @Override
    public void save(User theUser) {
        userRepo.save(theUser);
    }

    @Override
    public void updateIsEmailVerified(String otp) {
        User theUser = userRepo.findByotp(otp);
        if (theUser != null) {
            theUser.setEmailVerified(true);
            theUser.setOtp(null);
            userRepo.save(theUser);
        }
    }

    @Override
    public Boolean changePassword(String userId, ChangePasswordReq changePasswordReq) {
        Optional<User> userOpt = userRepo.findById(userId);

        if (!userOpt.isPresent()) {
            return false;
        }

        User user = userOpt.get();
        try {
            if (passwordEncoded.matches(changePasswordReq.getOldPassword(), user.getPassword())) {
                user.setPassword(passwordEncoded.encode(changePasswordReq.getNewPassWord()));
                userRepo.save(user);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public User findById(String userId) {
        Optional<User> userOpt = userRepo.findById(userId);
        return userOpt.orElse(null);
    }
}
