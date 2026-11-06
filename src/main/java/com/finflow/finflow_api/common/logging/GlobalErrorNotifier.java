
package com.finflow.finflow_api.common.logging;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GlobalErrorNotifier {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalErrorNotifier.class);

    public void notifyError(
            String message,
            Throwable exception,
            HttpServletRequest request) {

        logger.error(
                "GLOBAL ERROR | Method: {} | Endpoint: {} | Message: {}",
                request.getMethod(),
                request.getRequestURI(),
                message,
                exception
        );
    }
}
