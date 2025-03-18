@Slf4j
@Component
public class MilestoneDeadLetterHandler {

    public MilestoneDeadLetterHandler() {
    }

    @RabbitListener(queues = MilestoneRabbitMQConfig.MILESTONE_DLQ)
    public void handleDeadLetter(
        final Message failedMessage,
        @Header(AmqpHeaders.DEATH_REASON) final String reason,
        @Header("x-first-death-exchange") final String exchange,
        @Header("x-first-death-queue") final String queue,
        @Header("x-first-death-reason") final String firstReason) {

        log.error("Handling dead letter message from queue: {}", queue);
        log.error("Original exchange: {}", exchange);
        log.error("Death reason: {}", reason);
        log.error("First death reason: {}", firstReason);
        log.error("Message: {}", failedMessage);

        // TODO: Implement recovery strategy
        // Options:
        // 1. Store in database for manual review
        // 2. Retry with backoff
        // 3. Send notification to admin
        // 4. Move to parking lot queue
    }
}
