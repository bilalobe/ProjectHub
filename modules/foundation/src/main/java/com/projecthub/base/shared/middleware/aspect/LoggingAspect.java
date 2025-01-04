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

    @Before("execution(* com.projecthub.service.*.*(..))")
    public void logBeforeMethodExecution(JoinPoint joinPoint) {
        if (logger.isInfoEnabled()) {
            logger.info("Entering method: {} with arguments: {}", joinPoint.getSignature().toShortString(), formatArgs(joinPoint.getArgs()));
        }
    }

    @AfterReturning(pointcut = "execution(* com.projecthub.service.*.*(..))", returning = "result")
    public void logAfterMethodExecution(JoinPoint joinPoint, Object result) {
        if (logger.isInfoEnabled()) {
            logger.info("Exiting method: {} with result: {}", joinPoint.getSignature().toShortString(), result != null ? result : "null");
        }
    }

    @AfterThrowing(pointcut = "execution(* com.projecthub.service.*.*(..))", throwing = "exception")
    public void logAfterMethodException(JoinPoint joinPoint, Throwable exception) {
        if (logger.isErrorEnabled()) {
            logger.error("Exception in method: {} with message: {}", joinPoint.getSignature().toShortString(), exception.getMessage(), exception);
        }
    }

    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "none";
        }
        StringBuilder formattedArgs = new StringBuilder();
        for (Object arg : args) {
            formattedArgs.append(arg != null ? arg.toString() : "null").append(", ");
        }
        return formattedArgs.substring(0, formattedArgs.length() - 2);
    }
}
