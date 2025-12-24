package bankproject.shared.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Event published when a bank account is created in Account Service
 * Consumed by: Notification Service
 * Action: Send account creation confirmation email
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountCreatedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("eventId")
    private String eventId;

    @JsonProperty("eventType")
    private String eventType = "account.created";

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("accountNo")
    private Long accountNo;

    @JsonProperty("accountType")
    private String accountType;

    @JsonProperty("balance")
    private Double balance;

    @JsonProperty("createdAt")
    private LocalDateTime createdAt;
}
