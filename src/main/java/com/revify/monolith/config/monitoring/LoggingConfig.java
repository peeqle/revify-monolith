package com.revify.monolith.config.monitoring;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import io.sentry.Sentry;
import io.sentry.SentryOptions;
import io.sentry.logback.SentryAppender;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
public class LoggingConfig {

    private final SentryOptions sentryOptions;

    @PostConstruct
    public void initSentry() {
        Sentry.init(
                options -> {
                    options.setDsn(sentryOptions.getDsn());
                    options.setTracesSampleRate(sentryOptions.getTracesSampleRate());
                    options.setDebug(sentryOptions.isDebug());
                    options.setEnvironment(sentryOptions.getEnvironment());
                });
    }

    @Bean
    public LoggerContext loggerContext() {
        return (LoggerContext) LoggerFactory.getILoggerFactory();
    }

    @Bean
    public ConsoleAppender<ILoggingEvent> consoleAppender(LoggerContext context) {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n");
        encoder.start();

        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(context);
        consoleAppender.setEncoder(encoder);
        consoleAppender.start();

        return consoleAppender;
    }

    @Bean
    public PatternLayoutEncoder patternLayoutEncoder() {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n");
        return encoder;
    }

    @Bean
    public SentryAppender sentryAppender(LoggerContext context) {
        SentryAppender sentryAppender = new SentryAppender();
        sentryAppender.setContext(context);
        sentryAppender.setOptions(sentryOptions);
        sentryAppender.setMinimumEventLevel(Level.WARN);
        sentryAppender.setMinimumBreadcrumbLevel(Level.INFO);

        // Add a filter to exclude health check logs
        sentryAppender.addFilter(
                new Filter<ILoggingEvent>() {
                    @Override
                    public FilterReply decide(ILoggingEvent event) {
                        String loggerName = event.getLoggerName();
                        String message = event.getFormattedMessage();

                        if ((loggerName != null
                                && loggerName.contains("org.springframework.boot.actuate.health"))
                                || (message != null && message.matches(".*(/actuator/health|HealthIndicator).*"))) {
                            return FilterReply.DENY;
                        }

                        return FilterReply.NEUTRAL;
                    }
                });

        sentryAppender.start();

        return sentryAppender;
    }

    @Bean
    public Logger rootLogger(
            LoggerContext context,
            ConsoleAppender<ILoggingEvent> consoleAppender,
            SentryAppender sentryAppender) {
        Logger rootLogger = context.getLogger("ROOT");
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(consoleAppender);
        rootLogger.addAppender(sentryAppender);

        return rootLogger;
    }
}
