// package com.projecthub.core.config;

// import com.yubico.webauthn.WebAuthnManager;
// import com.yubico.webauthn.data.WebAuthnAuthenticationContextValidator;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// @Configuration
// public class WebAuthnConfig {

//     @Bean
//     public WebAuthnManager webAuthnManager() {
//         return WebAuthnManager.createNonStrictWebAuthnManager();
//     }

//     @Bean
//     public WebAuthnAuthenticationContextValidator webAuthnAuthenticationContextValidator() {
//         return new WebAuthnAuthenticationContextValidator();
//     }
// }