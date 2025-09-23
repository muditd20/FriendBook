package com.friendbook.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(
						auth -> auth.requestMatchers("/css/**", "/js/**", "/images/**", "/auth/**", "/h2-console/**")
								.permitAll().requestMatchers("/user/**").permitAll().anyRequest().authenticated())
				// ❌ remove this .formLogin(...) block completely
				// .formLogin(form -> form
				// .loginPage("/auth/login")
				// .permitAll()
				// )
				.logout(logout -> logout.permitAll())
				.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())); // H2 console

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
