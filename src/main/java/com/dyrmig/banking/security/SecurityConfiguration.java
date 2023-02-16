package com.dyrmig.banking.security;

import com.dyrmig.banking.filters.CustomAuthenticationFilter;
import com.dyrmig.banking.filters.CustomAuthorizationFilter;
import com.dyrmig.banking.service.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {
    // UserDetailsService is an interface provided by Spring Security that defines a way to retrieve user information
    // Implementation is in CustomUserDetailsService
    @Autowired
    private UserDetailsService userDetailsService;

    // Autowired instance of the AuthenticationManagerBuilder
    @Autowired
    private AuthenticationManagerBuilder authManagerBuilder;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CustomAuthenticationFilter instance created
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authManagerBuilder.getOrBuild());
        // set the URL that the filter should process
        customAuthenticationFilter.setFilterProcessesUrl("/login");
        // disable CSRF protection
        http.csrf().disable();
        // set the session creation policy to stateless
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // set up authorization for different request matchers and user roles
        http.authorizeHttpRequests() // GET /hello/user
                .requestMatchers(HttpMethod.POST, "/admins").hasRole("ADMIN") //change to .permitAll() be able to create the admin if there are no admins
                .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN") //get all users (admins and accountHolders)
                .requestMatchers(HttpMethod.POST, "/accountholders").hasRole("ADMIN") //create accountHolders
                .requestMatchers(HttpMethod.POST, "/accountholders/*/checking").hasRole("ADMIN") //create checking account
                .requestMatchers(HttpMethod.POST, "/accountholders/*/savings").hasRole("ADMIN") //create savings account
                .requestMatchers(HttpMethod.POST, "/accountholders/*/creditcard").hasRole("ADMIN") //create creditcard account
                .requestMatchers(HttpMethod.PATCH, "/accounts/*/subtract").hasRole("ADMIN") //subtract amount from account
                .requestMatchers(HttpMethod.PATCH, "/accounts/*/add").hasRole("ADMIN") //add amount to account
                .requestMatchers(HttpMethod.POST, "/accountholders/*/accounts/*/transfer").hasRole("USER") //transfer amount from account to account
                .requestMatchers(HttpMethod.GET, "/accountholders/*/accounts").hasAnyRole("USER", "ADMIN") //all accounts of an AccountHolder
                .requestMatchers(HttpMethod.GET, "/accounts/*").hasAnyRole("USER", "ADMIN") //get one account
                .requestMatchers(HttpMethod.GET, "/accountholders/*").hasAnyRole("USER", "ADMIN") //get one accountholder


                .requestMatchers(HttpMethod.GET, "/test2").hasAnyRole("USER", "ADMIN")

                .anyRequest().denyAll(); // deny any other route to any non-authenticated client

        // add the custom authentication filter to the http security object
        http.addFilter(customAuthenticationFilter);
        // Add the custom authorization filter before the standard authentication filter.
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        // Build the security filter chain to be returned.
        return http.build();

    }
}
