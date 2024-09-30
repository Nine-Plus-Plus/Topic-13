/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project.security;

import com.project.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 *
 * @author trinh
 */
@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    
    @Autowired
    private JWTAuthFilter jWTAuthFilter;
    
    private final String[] ADMIN_LIST = {"api/admin/**"};
    private final String[] STUDENT_LIST = {"api/student/**"};
    private final String[] MENTOR_LIST = {"api/mentor/**"};
    private final String[] ADMINUSER_LIST ={"/api/user/**"};
    private final String[] PUBLIC_LIST = {"api/auth/**","api/public/**"};
    private final String[] SWAGGERUI = {"/swagger-ui/**", "/v3/api-docs/**","/swagger-ui.html"};
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(request -> request
                        .requestMatchers(SWAGGERUI).permitAll()
                        .requestMatchers(PUBLIC_LIST).permitAll()
                        .requestMatchers(ADMIN_LIST).hasAuthority("ADMIN")
                        .requestMatchers(MENTOR_LIST).hasAuthority("MENTOR")
                        .requestMatchers(STUDENT_LIST).hasAuthority("STUDENT")
                        .requestMatchers(ADMINUSER_LIST).hasAnyAuthority("STUDENT", "MENTOR", "ADMIN")
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jWTAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return httpSecurity.build();        
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
}
