package com.nagarro.nagp.AuthService.config;

import com.nagarro.nagp.AuthService.entity.Role;
import com.nagarro.nagp.AuthService.entity.User;
import com.nagarro.nagp.AuthService.enums.RoleEnum;
import com.nagarro.nagp.AuthService.exception.UserNotFoundException;
import com.nagarro.nagp.AuthService.jwt.AuthEntryPointJwt;
import com.nagarro.nagp.AuthService.jwt.AuthTokenFilter;
import com.nagarro.nagp.AuthService.repository.RoleRepository;
import com.nagarro.nagp.AuthService.repository.UserRepository;
import com.nagarro.nagp.AuthService.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                            .requestMatchers("/h2-console/**").permitAll()
                            .requestMatchers("/login", "/validate-token", "/hello").permitAll()
                            .anyRequest().authenticated()
                )
                .sessionManagement(
                    session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }

    @Bean
    public CustomUserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
}
