package bankproject.customer.event;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import bankproject.customer.entity.UserDetail;
import bankproject.customer.repository.UserDetailRepository;
import bankproject.shared.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * Event listener for Customer Service
 * Listens to events from other services and updates local database
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserSyncEventListener {

    private final UserDetailRepository userDetailRepository;

    /**
     * Consumes UserCreatedEvent from Authentication Service
     * Creates a corresponding UserDetail record in customer-db
     * 
     * @return Consumer function for Spring Cloud Stream
     */
    @Bean
    public Consumer<UserCreatedEvent> userCreatedEventConsumer() {
        return event -> {
            try {
                log.info("Received UserCreatedEvent for userId: {}", event.getUserId());

                // Check if UserDetail already exists
                if (userDetailRepository.findByUserId(event.getUserId()).isPresent()) {
                    log.warn("UserDetail already exists for userId: {}", event.getUserId());
                    return;
                }

                // Create UserDetail in customer-db based on user.created event
                UserDetail userDetail = UserDetail.builder()
                        .userId(event.getUserId())
                        .mobile(event.getPhone())
                        .build();

                UserDetail saved = userDetailRepository.save(userDetail);
                log.info("UserDetail created successfully for userId: {}", saved.getUserId());

            } catch (Exception e) {
                log.error("Error processing UserCreatedEvent: ", e);
                // TODO: Consider storing failed events for retry
            }
        };
    }
}
