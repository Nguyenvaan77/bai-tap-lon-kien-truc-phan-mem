package bankproject.authentication.controller;

import java.sql.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bankproject.authentication.entity.Role;
import bankproject.authentication.entity.User;
import bankproject.authentication.service.SignUpService;
import bankproject.authentication.client.NotificationClient;
import bankproject.authentication.dto.MailRequest;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
public class SignUpController {

    @Autowired
    private SignUpService signUpService;

    @Autowired
    private NotificationClient notificationClient;

    private String generateOTP() {
        return String.format("%06d", new java.util.Random().nextInt(999999));
    }

    @PostMapping("/signup")
    public ResponseEntity<User> Signup(@RequestBody User user) {
        if (signUpService.findByEmail(user.getEmail()) != null) {
            return new ResponseEntity<User>(HttpStatus.CONFLICT);
        }

        String userid = UUID.randomUUID().toString();
        String otp = generateOTP();
        user.setUserId(userid);
        user.setOtp(otp);
        user.setRole(Role.USER);
        user.setCreatedDate(new Date(System.currentTimeMillis()));
        User theUser = signUpService.createUser(user);

        // Send OTP email via notification service
        try {
            MailRequest mailRequest = MailRequest.builder()
                    .to(user.getEmail())
                    .subject("Registration OTP code")
                    .body("This is 6 digit otp code: " + otp
                            + "\n\n Click here to verify: http://localhost:3000/signup/otp"
                            + "\n\nThank you.")
                    .build();
            notificationClient.sendMail(mailRequest);
        } catch (Exception e) {
            System.out.println("Failed to send OTP email: " + e.getMessage());
        }

        return new ResponseEntity<User>(theUser, HttpStatus.OK);
    }

    @PostMapping("/otp")
    public ResponseEntity<?> checkOTP(@RequestBody User theUser) {
        if (signUpService.findByOTP(theUser.getOtp()) == null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        signUpService.updateIsEmailVerified(theUser.getOtp());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/resend-otp/{userId}")
    public ResponseEntity<?> resendOTP(@PathVariable String userId) {
        User user = signUpService.findById(userId);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String otp = generateOTP();
        user.setOtp(otp);
        signUpService.save(user);

        // Resend OTP email via notification service
        try {
            MailRequest mailRequest = MailRequest.builder()
                    .to(user.getEmail())
                    .subject("Registration OTP code")
                    .body("This is 6 digit otp code: " + otp
                            + "\n\n Click here to verify: http://localhost:3000/signup/otp"
                            + "\n\nThank you.")
                    .build();
            notificationClient.sendMail(mailRequest);
        } catch (Exception e) {
            System.out.println("Failed to send OTP email: " + e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
