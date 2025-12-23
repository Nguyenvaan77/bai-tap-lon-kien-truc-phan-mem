package bankproject.customer.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bankproject.customer.entity.UserDetail;
import bankproject.customer.exception.UserNotFoundException;
import bankproject.customer.repository.UserDetailRepository;
import bankproject.customer.service.ProfileService;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserDetailRepository userDetailRepo;

    @Override
    public UserDetail createUserProfile(UserDetail userDetails, String userId) throws UserNotFoundException {

        if (userDetails.getAdhaar() == null || userDetails.getPan() == null || userDetails.getMobile() == null) {
            throw new UserNotFoundException("Provide mandatory fields");
        }

        userDetails.setUserId(userId);
        UserDetail savedDetails = userDetailRepo.save(userDetails);
        return savedDetails;
    }

    @Override
    public UserDetail updateUserProfile(UserDetail userDetails, String userId) throws UserNotFoundException {

        if (userDetails.getAdhaar() == null || userDetails.getPan() == null || userDetails.getMobile() == null) {
            throw new UserNotFoundException("Provide mandatory fields");
        }

        userDetails.setUserId(userId);
        UserDetail savedDetails = userDetailRepo.save(userDetails);
        return savedDetails;
    }

    @Override
    public UserDetail getUserProfile(String userId) throws UserNotFoundException {
        Optional<UserDetail> userDetailOpt = userDetailRepo.findByUserId(userId);

        if (!userDetailOpt.isPresent()) {
            throw new UserNotFoundException("User profile not found");
        }

        return userDetailOpt.get();
    }
}
