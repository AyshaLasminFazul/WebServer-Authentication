
package com.finflow.finflow_api.common.logging;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class GlobalErrorNotifierTest {

    private GlobalErrorNotifier globalErrorNotifier;
    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        globalErrorNotifier = new GlobalErrorNotifier();

        logger = (Logger) LoggerFactory.getLogger(GlobalErrorNotifier.class);

        listAppender = new ListAppender<>();
        listAppender.start();

        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
        listAppender.stop();
    }

    @Test
    void notifyError_ShouldLogRequestDetailsAndErrorMessage() {

        // Arrange
        HttpServletRequest request =
                new MockHttpServletRequest("POST", "/api/auth/login");

        RuntimeException exception =
                new RuntimeException("Database connection failed");

        // Act
        globalErrorNotifier.notifyError(
                "User login failed", exception, request);

        // Assert
        assertEquals(1, listAppender.list.size());

        ILoggingEvent logEvent = listAppender.list.get(0);

        String logMessage = logEvent.getFormattedMessage();

        assertTrue(logMessage.contains("GLOBAL ERROR"));
        assertTrue(logMessage.contains("POST"));
        assertTrue(logMessage.contains("/api/auth/login"));
        assertTrue(logMessage.contains("User login failed"));

        assertNotNull(logEvent.getThrowableProxy());
    }
}
