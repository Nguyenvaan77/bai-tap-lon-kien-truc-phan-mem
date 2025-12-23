package bankproject.authentication.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import bankproject.authentication.dto.LoginRequest;
import bankproject.authentication.entity.User;
import bankproject.authentication.exception.UserNotFoundException;
import bankproject.authentication.repository.UserRepository;
import bankproject.authentication.service.LoginService;
import java.util.Optional;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findByEmail(LoginRequest loginReq) throws UserNotFoundException {
        Optional<User> userOpt = userRepository.findByEmail(loginReq.getEmail());

        if (!userOpt.isPresent())
            throw new UserNotFoundException("Email does not exist");

        User user = userOpt.get();

        if (!user.isEnabled())
            throw new UserNotFoundException("Email Not Verified");

        boolean isCorrect = passwordEncoder.matches(loginReq.getPassword(), user.getPassword());

        if (!isCorrect)
            throw new UserNotFoundException("Invalid credentials");
        else
            return user;
    }
}
