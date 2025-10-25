package web2.lab1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.auth0.audience:}")
    private String audience;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/ticket/**", "/actuator/**").permitAll()
                        .requestMatchers("/tickets").authenticated() // user must be logged in via OIDC (or have token)
                        .requestMatchers("/new-round", "/close", "/store-results").authenticated()
                        .anyRequest().authenticated()
                )
                .oauth2Login(Customizer.withDefaults()) // OIDC interactive login for users (Auth0)
                .oauth2ResourceServer(r -> r
                        .jwt(jwt -> jwt.decoder(jwtDecoder))
                )
                .logout(logout -> logout.logoutSuccessUrl("/").permitAll());

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri) {
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuerUri);
        if (audience != null && !audience.isBlank()) {
            OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
            OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
            OAuth2TokenValidator<Jwt> withIssuerAndAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
            jwtDecoder.setJwtValidator(withIssuerAndAudience);
        }
        return jwtDecoder;
    }
}

