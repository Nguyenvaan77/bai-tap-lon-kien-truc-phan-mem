package bankproject.customer.service;

import bankproject.customer.entity.UserDetail;
import bankproject.customer.exception.UserNotFoundException;

public interface ProfileService {
    public UserDetail createUserProfile(UserDetail userDetails, String userId) throws UserNotFoundException;

    public UserDetail updateUserProfile(UserDetail userDetails, String userId) throws UserNotFoundException;

    public UserDetail getUserProfile(String userId) throws UserNotFoundException;
}
