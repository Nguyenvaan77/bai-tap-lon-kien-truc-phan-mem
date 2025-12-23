package bankproject.authentication.service;

import bankproject.authentication.entity.User;
import bankproject.authentication.dto.LoginRequest;
import bankproject.authentication.exception.UserNotFoundException;

public interface LoginService {
    public User findByEmail(LoginRequest loginReq) throws UserNotFoundException;
}
