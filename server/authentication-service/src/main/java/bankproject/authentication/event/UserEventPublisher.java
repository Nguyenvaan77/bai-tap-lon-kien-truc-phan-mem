package bankproject.authentication.event;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import bankproject.authentication.entity.User;
import bankproject.shared.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event publisher for Authentication Service
 * Publishes events when user-related actions occur
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventPublisher {

    private final StreamBridge streamBridge;

    /**
     * Publishes UserCreatedEvent when a new user signs up
     * 
     * @param user The newly created user
     * @return true if published successfully, false otherwise
     */
    public boolean publishUserCreatedEvent(User user) {
        try {
            UserCreatedEvent event = UserCreatedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("user.created")
                    .timestamp(LocalDateTime.now())
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .firstname(user.getFirstname())
                    .lastname(user.getLastname())
                    .phone("") // Phone not available in User entity
                    .role(user.getRole() != null ? user.getRole().toString() : "USER")
                    .build();

            // Publish to RabbitMQ/Kafka using Spring Cloud Stream
            boolean success = streamBridge.send(
                    "user-created-out-0",
                    MessageBuilder.withPayload(event).build());

            if (success) {
                log.info("UserCreatedEvent published for userId: {}", user.getUserId());
            } else {
                log.error("Failed to publish UserCreatedEvent for userId: {}", user.getUserId());
            }

            return success;

        } catch (Exception e) {
            log.error("Error publishing UserCreatedEvent: ", e);
            return false;
        }
    }
}
