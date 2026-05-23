package com.nagarro.nagp.AuthService.config;

import com.nagarro.nagp.AuthService.entity.Role;
import com.nagarro.nagp.AuthService.entity.User;
import com.nagarro.nagp.AuthService.jwt.AuthEntryPointJwt;
import com.nagarro.nagp.AuthService.jwt.AuthTokenFilter;
import com.nagarro.nagp.AuthService.repository.RoleRepository;
import com.nagarro.nagp.AuthService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;
import java.util.UUID;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    DataSource dataSource;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                            .requestMatchers("/h2-console/**").permitAll()
                            .requestMatchers("/auth/**").permitAll()
                            .anyRequest().authenticated());
        http.sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));
        http.addFilterBefore(authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {

            Role employeeRole = roleRepository
                    .findByName("EMPLOYEE")
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setName("EMPLOYEE");
                        return roleRepository.save(role);
                    });

            Role managerRole = roleRepository
                    .findByName("MANAGER")
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setName("MANAGER");
                        return roleRepository.save(role);
                    });

            User manager;
            if (userRepository.findByUsername("manager1").isEmpty()) {
                manager = new User();
                manager.setId(UUID.randomUUID().toString());
                manager.setUsername("manager1");
                manager.setPassword(passwordEncoder().encode("manager1"));
                manager.setEmployeeCode("M001");
                manager.setName("Manager One");
                manager.setEmail("manager@test.com");
                manager.setActive(true);

                manager.getRoles().add(managerRole);
                manager.getRoles().add(employeeRole);

                userRepository.save(manager);
            }

            if (userRepository.findByUsername("user1").isEmpty()) {
                manager = userRepository.findByUsername("manager1")
                        .orElseThrow(() ->
                                new RuntimeException("Manager not found"));

                User employee = new User();
                employee.setId(UUID.randomUUID().toString());
                employee.setUsername("user1");
                employee.setPassword(passwordEncoder().encode("user1"));
                employee.setEmployeeCode("E001");
                employee.setName("Employee One");
                employee.setEmail("user@test.com");
                employee.setManager(manager);
                employee.setActive(true);

                employee.getRoles().add(employeeRole);

                userRepository.save(employee);
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration builder) throws Exception {
        return builder.getAuthenticationManager();
    }
}
