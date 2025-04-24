package com.truelanz.catalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

//Classe para liberar acesso aos endpoints com o spring security habilitado no pom.xml. (temporário para testes)
@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable());
		http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
		return http.build();
	}
}
