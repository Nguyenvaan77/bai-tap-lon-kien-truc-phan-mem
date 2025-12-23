package bankproject.account.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import bankproject.account.dto.MailRequest;

@FeignClient(name = "notification-service", url = "http://localhost:8084")
public interface NotificationClient {

    @PostMapping("/api/v1/mail/send")
    ResponseEntity<?> sendMail(@RequestBody MailRequest mailRequest);
}
