package pj.gob.pe.gateway.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pj.gob.pe.gateway.exceptionhandling.CustomAccessDeniedHandler;
import pj.gob.pe.gateway.filter.CsrfCookieFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class ProjectSecurityConfig {

    // Configuración de CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList("http://localhost:4200")); // Origen permitido
        config.setAllowedMethods(Collections.singletonList("*")); // Métodos permitidos
        config.setAllowCredentials(true); // Permitir credenciales
        config.setAllowedHeaders(Collections.singletonList("*")); // Cabeceras permitidas
        config.setExposedHeaders(Arrays.asList("Authorization")); // Cabeceras expuestas
        config.setMaxAge(3600L); // Tiempo máximo de caché para CORS
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Aplicar a todas las rutas
        return source;
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        //CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();
        http.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configuración de CORS
                .cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource()))

                .csrf(csrf -> csrf.disable())
                //.csrf(csrfConfig -> csrfConfig.csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)
//                .ignoringRequestMatchers("/contact", "/register")
//                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
//                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
//                .requiresChannel(rcc -> rcc.anyRequest().requiresInsecure()) // Only HTTP
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/api/security/v1/auth/login").permitAll()
                        .requestMatchers("/api/security/v1/auth/verify-session").permitAll()
                        .requestMatchers("/api/security/v1/auth/logout").permitAll()
                        .requestMatchers("/api/security/**").authenticated()
                        .requestMatchers("/api/consultaia/**").authenticated()
                        .requestMatchers("/v1/test/*").authenticated());
        http.oauth2ResourceServer(rsc -> rsc.jwt(jwtConfigurer ->
                jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter)));
        http.exceptionHandling(ehc -> ehc.accessDeniedHandler(new CustomAccessDeniedHandler()));
        return http.build();
    }
}

//                        .requestMatchers("/myAccount").hasRole("USER")
//                        .requestMatchers("/myBalance").hasAnyRole("USER", "ADMIN")
//                        .requestMatchers("/myLoans").authenticated()
//                        .requestMatchers("/myCards").hasRole("USER")
//                        .requestMatchers("/user").authenticated()
//                        .requestMatchers("/notices", "/contact", "/error", "/register").permitAll());
/*http.oauth2ResourceServer(rsc -> rsc.opaqueToken(otc -> otc.authenticationConverter(new KeycloakOpaqueRoleConverter())
                .introspectionUri(this.introspectionUri).introspectionClientCredentials(this.clientId,this.clientSecret)));*/