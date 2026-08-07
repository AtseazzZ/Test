package com.gzhu.csnet.kclab.classics100common.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties(RabbitMqReliabilityProperties.class)
public class RabbitMQConfig {

    public static final String HEARTBEAT_QUEUE = "reading.heartbeat.queue";
    public static final String HEARTBEAT_EXCHANGE = "reading.heartbeat.exchange";
    public static final String HEARTBEAT_ROUTING_KEY = "reading.heartbeat";

    public static final String ASYNC_TASK_EXCHANGE = "async.task.exchange";
    public static final String ASYNC_NOTIFICATION_QUEUE = "async.notification.queue";
    public static final String ASYNC_WECHAT_QUEUE = "async.wechat.queue";
    public static final String ASYNC_EXPORT_QUEUE = "async.export.queue";
    public static final String ASYNC_ARCHIVE_QUEUE = "async.archive.queue";
    public static final String ASYNC_NOTIFICATION_ROUTING_KEY = "task.notification";
    public static final String ASYNC_WECHAT_ROUTING_KEY = "task.wechat";
    public static final String ASYNC_EXPORT_ROUTING_KEY = "task.export";
    public static final String ASYNC_ARCHIVE_ROUTING_KEY = "task.archive";

    public static final String MQ_RETRY_EXCHANGE = "mq.retry.exchange";
    public static final String MQ_DEAD_LETTER_EXCHANGE = "mq.dead-letter.exchange";

    @Bean
    public Queue heartbeatQueue() {
        return durableReliableQueue(HEARTBEAT_QUEUE);
    }

    @Bean
    public DirectExchange heartbeatExchange() {
        return ExchangeBuilder.directExchange(HEARTBEAT_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Binding heartbeatBinding(Queue heartbeatQueue, DirectExchange heartbeatExchange) {
        return BindingBuilder.bind(heartbeatQueue)
                .to(heartbeatExchange)
                .with(HEARTBEAT_ROUTING_KEY);
    }

    @Bean
    public DirectExchange asyncTaskExchange() {
        return ExchangeBuilder.directExchange(ASYNC_TASK_EXCHANGE)
                .durable(true)
                .build();
    }

    @Bean
    public Queue asyncNotificationQueue() {
        return durableReliableQueue(ASYNC_NOTIFICATION_QUEUE);
    }

    @Bean
    public Queue asyncWechatQueue() {
        return durableReliableQueue(ASYNC_WECHAT_QUEUE);
    }

    @Bean
    public Queue asyncExportQueue() {
        return durableReliableQueue(ASYNC_EXPORT_QUEUE);
    }

    @Bean
    public Queue asyncArchiveQueue() {
        return durableReliableQueue(ASYNC_ARCHIVE_QUEUE);
    }

    @Bean
    public Binding asyncNotificationBinding(Queue asyncNotificationQueue, DirectExchange asyncTaskExchange) {
        return BindingBuilder.bind(asyncNotificationQueue)
                .to(asyncTaskExchange)
                .with(ASYNC_NOTIFICATION_ROUTING_KEY);
    }

    @Bean
    public Binding asyncWechatBinding(Queue asyncWechatQueue, DirectExchange asyncTaskExchange) {
        return BindingBuilder.bind(asyncWechatQueue)
                .to(asyncTaskExchange)
                .with(ASYNC_WECHAT_ROUTING_KEY);
    }

    @Bean
    public Binding asyncExportBinding(Queue asyncExportQueue, DirectExchange asyncTaskExchange) {
        return BindingBuilder.bind(asyncExportQueue)
                .to(asyncTaskExchange)
                .with(ASYNC_EXPORT_ROUTING_KEY);
    }

    @Bean
    public Binding asyncArchiveBinding(Queue asyncArchiveQueue, DirectExchange asyncTaskExchange) {
        return BindingBuilder.bind(asyncArchiveQueue)
                .to(asyncTaskExchange)
                .with(ASYNC_ARCHIVE_ROUTING_KEY);
    }

    @Bean
    public Declarables mqReliabilityTopology(RabbitMqReliabilityProperties properties) {
        List<Declarable> declarables = new ArrayList<>();
        DirectExchange retryExchange = ExchangeBuilder.directExchange(MQ_RETRY_EXCHANGE)
                .durable(true)
                .build();
        DirectExchange deadLetterExchange = ExchangeBuilder.directExchange(MQ_DEAD_LETTER_EXCHANGE)
                .durable(true)
                .build();
        declarables.add(retryExchange);
        declarables.add(deadLetterExchange);

        for (ReliableRoute route : reliableRoutes()) {
            Queue deadLetterQueue = QueueBuilder.durable(deadLetterQueueName(route.queueName()))
                    .build();
            declarables.add(deadLetterQueue);
            declarables.add(BindingBuilder.bind(deadLetterQueue)
                    .to(deadLetterExchange)
                    .with(deadLetterRoutingKey(route.queueName())));

            int maxRetryAttempts = Math.max(1, properties.getMaxRetryAttempts());
            for (int attempt = 1; attempt <= maxRetryAttempts; attempt++) {
                long ttlMillis = Math.max(1, properties.retryIntervalForAttempt(attempt).toMillis());
                Queue retryQueue = QueueBuilder.durable(retryQueueName(route.queueName(), attempt))
                        .ttl(Math.toIntExact(Math.min(ttlMillis, Integer.MAX_VALUE)))
                        .deadLetterExchange(route.exchangeName())
                        .deadLetterRoutingKey(route.routingKey())
                        .build();
                declarables.add(retryQueue);
                declarables.add(BindingBuilder.bind(retryQueue)
                        .to(retryExchange)
                        .with(retryRoutingKey(route.routingKey(), attempt)));
            }
        }
        return new Declarables(declarables);
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    public static List<ReliableRoute> reliableRoutes() {
        return List.of(
                new ReliableRoute(HEARTBEAT_QUEUE, HEARTBEAT_EXCHANGE, HEARTBEAT_ROUTING_KEY, "reading-heartbeat"),
                new ReliableRoute(ASYNC_NOTIFICATION_QUEUE, ASYNC_TASK_EXCHANGE, ASYNC_NOTIFICATION_ROUTING_KEY, "async-notification"),
                new ReliableRoute(ASYNC_WECHAT_QUEUE, ASYNC_TASK_EXCHANGE, ASYNC_WECHAT_ROUTING_KEY, "async-wechat"),
                new ReliableRoute(ASYNC_EXPORT_QUEUE, ASYNC_TASK_EXCHANGE, ASYNC_EXPORT_ROUTING_KEY, "async-export"),
                new ReliableRoute(ASYNC_ARCHIVE_QUEUE, ASYNC_TASK_EXCHANGE, ASYNC_ARCHIVE_ROUTING_KEY, "async-archive")
        );
    }

    public static ReliableRoute routeByQueue(String queueName) {
        return reliableRoutes().stream()
                .filter(route -> route.queueName().equals(queueName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported reliable MQ queue: " + queueName));
    }

    public static String retryQueueName(String queueName, int attempt) {
        return queueName + ".retry." + attempt;
    }

    public static String retryRoutingKey(String routingKey, int attempt) {
        return routingKey + ".retry." + attempt;
    }

    public static String deadLetterQueueName(String queueName) {
        return queueName + ".dlq";
    }

    public static String deadLetterRoutingKey(String queueName) {
        return queueName + ".dead";
    }

    private Queue durableReliableQueue(String queueName) {
        return QueueBuilder.durable(queueName).build();
    }

    public record ReliableRoute(String queueName, String exchangeName, String routingKey, String businessType) {
    }
}
