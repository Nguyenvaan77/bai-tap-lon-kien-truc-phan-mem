package bankproject.account.event;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import bankproject.account.entity.BankAccount;
import bankproject.account.entity.Transactions;
import bankproject.shared.event.AccountCreatedEvent;
import bankproject.shared.event.TransactionCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event publisher for Account Service
 * Publishes events when account-related actions occur
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AccountEventPublisher {

    private final StreamBridge streamBridge;

    /**
     * Publishes AccountCreatedEvent when a new bank account is created
     * 
     * @param account The newly created bank account
     * @return true if published successfully
     */
    public boolean publishAccountCreatedEvent(BankAccount account) {
        try {
            AccountCreatedEvent event = AccountCreatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("account.created")
                    .timestamp(LocalDateTime.now())
                    .userId(account.getUserId())
                    .accountNo(account.getAccountno())
                    .accountType(account.getAccountType())
                    .balance(account.getBalance())
                    .createdAt(LocalDateTime.now())
                    .build();

            boolean success = streamBridge.send(
                    "account-created-out-0",
                    MessageBuilder.withPayload(event).build());

            if (success) {
                log.info("AccountCreatedEvent published for accountNo: {}", account.getAccountno());
            } else {
                log.error("Failed to publish AccountCreatedEvent for accountNo: {}", account.getAccountno());
            }

            return success;

        } catch (Exception e) {
            log.error("Error publishing AccountCreatedEvent: ", e);
            return false;
        }
    }

    /**
     * Publishes TransactionCompletedEvent when a transaction is completed
     * 
     * @param transaction The completed transaction
     * @return true if published successfully
     */
    public boolean publishTransactionCompletedEvent(Transactions transaction) {
        try {
            TransactionCompletedEvent event = TransactionCompletedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("transaction.completed")
                    .timestamp(LocalDateTime.now())
                    .transactionId((long) transaction.getTransactionId())
                    .fromAccount(transaction.getFromAccount())
                    .toAccount(transaction.getToAccount())
                    .amount(transaction.getAmount())
                    .status(transaction.getTransactionStatus())
                    .description(transaction.getDescription())
                    .transactionDate(LocalDateTime.now())
                    .build();

            boolean success = streamBridge.send(
                    "transaction-completed-out-0",
                    MessageBuilder.withPayload(event).build());

            if (success) {
                log.info("TransactionCompletedEvent published for transactionId: {}",
                        transaction.getTransactionId());
            } else {
                log.error("Failed to publish TransactionCompletedEvent for transactionId: {}",
                        transaction.getTransactionId());
            }

            return success;

        } catch (Exception e) {
            log.error("Error publishing TransactionCompletedEvent: ", e);
            return false;
        }
    }
}
