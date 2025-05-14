package com.truelanz.catalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
        // LIBERA iframes para vizualização de h2-console no navegador.
        .headers(headers -> headers.frameOptions(frame -> frame.disable())); 

		http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
		return http.build();
	}
}

