package bankproject.shared.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Event published when a transaction is completed in Account Service
 * Consumed by: Notification Service
 * Action: Send transaction confirmation email
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCompletedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("eventId")
    private String eventId;

    @JsonProperty("eventType")
    private String eventType = "transaction.completed";

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("transactionId")
    private Long transactionId;

    @JsonProperty("fromAccount")
    private Long fromAccount;

    @JsonProperty("toAccount")
    private Long toAccount;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("status")
    private String status;

    @JsonProperty("description")
    private String description;

    @JsonProperty("transactionDate")
    private LocalDateTime transactionDate;
}
