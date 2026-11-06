package com.finflow.finflow_api.config;

import com.finflow.finflow_api.auth.CustomAuthenticationEntryPoint;
import com.finflow.finflow_api.auth.JwtAuthenticationFilter;
import com.finflow.finflow_api.auth.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfiguration(CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http,JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception{

        http
                .csrf(csrf->csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(authenticationEntryPoint)
                )

                .authorizeHttpRequests(auth->auth.
                        requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        .anyRequest().authenticated()

                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

   return http.build();
   }

   @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService){
        return new JwtAuthenticationFilter(jwtService);
   }

}
