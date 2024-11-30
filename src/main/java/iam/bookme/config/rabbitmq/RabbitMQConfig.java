package iam.bookme.config.rabbitmq;

import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.core.AmqpTemplate;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    /*
     * Purpose:
     * - Declare queue for safety - ensures queue exists before consumer attempts to connect
     * - Validates queue configuration matches expected settings
     * - Provides resilience in case of service restarts
     * Behavior:
     * - Creates queue if it doesn't exist
     * - No-op if queue exists with matching configuration
     * - Throws exception if queue exists with different settings
     */
    @Bean
    public Queue queue() {
        return QueueBuilder.durable(queueName)
                // When messages fail, send to default exchange
                .withArgument("x-dead-letter-exchange", "")
                // Route failed messages to a queue named "<originalQueue>.dlq"
                .withArgument("x-dead-letter-routing-key", queueName + ".dlq")
                .build();
    }

    /* Dead Letter Queue for failed messages */
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(queueName + ".dlq")
                .build();
    }

    /*
     * Converts messages to/from JSON format
     * Required for proper deserialization of received messages
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /*
     * Configure RabbitTemplate with message converter
     * Used for any message handling operations
     * Note: Exchange and binding config not needed in consumer
     */
    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

}
