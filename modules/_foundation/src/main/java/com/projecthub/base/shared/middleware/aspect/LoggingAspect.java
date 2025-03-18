package com.projecthub.base.shared.middleware.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    public LoggingAspect() {
    }

    @Before("execution(* com.projecthub.service.*.*(..))")
    public void logBeforeMethodExecution(final JoinPoint joinPoint) {
        if (LoggingAspect.logger.isInfoEnabled()) {
            LoggingAspect.logger.info("Entering method: {} with arguments: {}", joinPoint.getSignature().toShortString(), LoggingAspect.formatArgs(joinPoint.getArgs()));
        }
    }

    @AfterReturning(pointcut = "execution(* com.projecthub.service.*.*(..))", returning = "result")
    public static void logAfterMethodExecution(final JoinPoint joinPoint, final Object result) {
        if (LoggingAspect.logger.isInfoEnabled()) {
            LoggingAspect.logger.info("Exiting method: {} with result: {}", joinPoint.getSignature().toShortString(), null != result ? result : "null");
        }
    }

    @AfterThrowing(pointcut = "execution(* com.projecthub.service.*.*(..))", throwing = "exception")
    public static void logAfterMethodException(final JoinPoint joinPoint, final Throwable exception) {
        if (LoggingAspect.logger.isErrorEnabled()) {
            LoggingAspect.logger.error("Exception in method: {} with message: {}", joinPoint.getSignature().toShortString(), exception.getMessage(), exception);
        }
    }

    private static String formatArgs(final Object[] args) {
        if (null == args || 0 == args.length) {
            return "none";
        }
        final StringBuilder formattedArgs = new StringBuilder();
        for (final Object arg : args) {
            formattedArgs.append(null != arg ? arg.toString() : "null").append(", ");
        }
        return formattedArgs.substring(0, formattedArgs.length() - 2);
    }
}
