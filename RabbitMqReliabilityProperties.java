package com.gzhu.csnet.kclab.classics100common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "c100.rabbitmq.reliability")
public class RabbitMqReliabilityProperties {

    /**
     * Number of delayed retry deliveries before a message is moved to DLQ.
     */
    private int maxRetryAttempts = 3;

    /**
     * Retry delays. Defaults to 1 minute, 5 minutes, and 30 minutes.
     */
    private List<Duration> retryIntervals = new ArrayList<>(List.of(
            Duration.ofMinutes(1),
            Duration.ofMinutes(5),
            Duration.ofMinutes(30)
    ));

    /**
     * How long a PROCESSING consume log may stay before it can be reclaimed.
     */
    private Duration processingTimeout = Duration.ofMinutes(30);

    public Duration retryIntervalForAttempt(int attempt) {
        if (retryIntervals == null || retryIntervals.isEmpty()) {
            return Duration.ofMinutes(1);
        }
        int index = Math.max(0, Math.min(attempt - 1, retryIntervals.size() - 1));
        return retryIntervals.get(index);
    }
}
