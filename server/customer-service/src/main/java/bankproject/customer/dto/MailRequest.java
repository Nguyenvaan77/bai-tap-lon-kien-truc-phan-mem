package bankproject.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MailRequest {
    private String to;
    private String subject;
    private String body;
}

/*
eyJhbGciOiJIUzUxMiJ9.eyJ1c2VySWQiOiJmYzc2MWRmMC01NmRjLTQzOTQtOWE1MS05ZGZjNDg5MjNiM2IiLCJzdWIiOiJuZ3V5ZW52YW5hQGV4YW1wbGUuY29tIiwiaWF0IjoxNzY2NTM3NDE3LCJleHAiOjE3NjY1NTU0MTd9.G6M-5S6NfGAFCTAokmkoGxIVBzFz_jrx0QkTY56zYHe0pkPjEuSbfI4lGGgj9g9YQDyba-ka4TVZPZKdkDHEAg
*/
