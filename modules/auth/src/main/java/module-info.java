module com.projecthub.auth {
    requires spring.context;
    requires spring.web;
    requires spring.security.core;
    requires spring.security.web;
    requires spring.beans;
    requires java.validation;
    requires spring.core;
    requires org.aspectj.weaver;
    requires javax.servlet.api;

    exports com.projecthub.auth.dto;
    exports com.projecthub.auth.exception;
    exports com.projecthub.auth.facade;
    exports com.projecthub.auth.service;
    exports com.projecthub.auth.annotation;
    exports com.projecthub.auth.util;
    exports com.projecthub.auth.config;

    opens com.projecthub.auth.dto to spring.core;
    opens com.projecthub.auth.config to spring.core;
}