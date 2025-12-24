package bankproject.notification.event;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import bankproject.notification.service.MailService;
import bankproject.shared.event.AccountCreatedEvent;
import bankproject.shared.event.TransactionCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.function.Consumer;

/**
 * Event listener for Notification Service
 * Listens to events from other services and sends notifications
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final MailService mailService;

    /**
     * Consumes AccountCreatedEvent from Account Service
     * Sends account creation confirmation email to user
     * 
     * @return Consumer function for Spring Cloud Stream
     */
    @Bean
    public Consumer<AccountCreatedEvent> accountCreatedEventConsumer() {
        return event -> {
            try {
                log.info("Received AccountCreatedEvent for accountNo: {}", event.getAccountNo());

                // TODO: Fetch user email from Auth Service (via REST call or cache)
                // For now, using placeholder
                String userEmail = "user@example.com";

                // Send account creation confirmation email
                String subject = "Account Created Successfully";
                String body = String.format(
                        "Dear User,\n\n" +
                                "Your account has been created successfully.\n\n" +
                                "Account Number: %d\n" +
                                "Account Type: %s\n" +
                                "Initial Balance: %,.2f\n\n" +
                                "Best regards,\n" +
                                "Online Banking Team",
                        event.getAccountNo(),
                        event.getAccountType(),
                        event.getBalance());

                mailService.transactionMail(userEmail, subject, body);
                log.info("Account creation confirmation email sent for accountNo: {}", event.getAccountNo());

            } catch (Exception e) {
                log.error("Error processing AccountCreatedEvent: ", e);
            }
        };
    }

    /**
     * Consumes TransactionCompletedEvent from Account Service
     * Sends transaction confirmation email to user
     * 
     * @return Consumer function for Spring Cloud Stream
     */
    @Bean
    public Consumer<TransactionCompletedEvent> transactionCompletedEventConsumer() {
        return event -> {
            try {
                log.info("Received TransactionCompletedEvent for transactionId: {}", event.getTransactionId());

                // TODO: Fetch user email from Account Service (via REST call or cache)
                // For now, using placeholder
                String userEmail = "user@example.com";

                // Send transaction confirmation email
                String subject = "Transaction Confirmation";
                String body = String.format(
                        "Dear User,\n\n" +
                                "Your transaction has been completed successfully.\n\n" +
                                "Transaction ID: %d\n" +
                                "From Account: %d\n" +
                                "To Account: %d\n" +
                                "Amount: %,.2f\n" +
                                "Status: %s\n" +
                                "Date & Time: %s\n\n" +
                                "Best regards,\n" +
                                "Online Banking Team",
                        event.getTransactionId(),
                        event.getFromAccount(),
                        event.getToAccount(),
                        event.getAmount(),
                        event.getStatus(),
                        event.getTransactionDate());

                mailService.transactionMail(userEmail, subject, body);
                log.info("Transaction confirmation email sent for transactionId: {}", event.getTransactionId());

            } catch (Exception e) {
                log.error("Error processing TransactionCompletedEvent: ", e);
            }
        };
    }
}
