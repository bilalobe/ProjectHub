@Slf4j
@Component
public class MilestoneDeadLetterHandler {

    @RabbitListener(queues = MilestoneRabbitMQConfig.MILESTONE_DLQ)
    public void handleDeadLetter(
            Message failedMessage,
            @Header(AmqpHeaders.DEATH_REASON) String reason,
            @Header("x-first-death-exchange") String exchange,
            @Header("x-first-death-queue") String queue,
            @Header("x-first-death-reason") String firstReason) {
        
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