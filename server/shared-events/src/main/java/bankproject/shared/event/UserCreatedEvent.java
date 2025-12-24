package bankproject.shared.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Event published when a new user is created in Authentication Service
 * Consumed by: Customer Service
 * Action: Create UserDetail record
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreatedEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("eventId")
    private String eventId;

    @JsonProperty("eventType")
    private String eventType = "user.created";

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("firstname")
    private String firstname;

    @JsonProperty("lastname")
    private String lastname;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("role")
    private String role;
}
