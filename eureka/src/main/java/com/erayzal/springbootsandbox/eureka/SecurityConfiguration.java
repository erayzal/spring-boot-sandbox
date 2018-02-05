package com.erayzal.springbootsandbox.eureka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Eureka clients couldn't register with CSRF enabled
        http.csrf().ignoringAntMatchers("/eureka/**");

        // Allow access to resources used by the eureka status page without authentication.
        // (as an exercice, this might not be a good idea in real life)
        http.authorizeRequests()
                .antMatchers("/eureka/css/**", "/eureka/fonts/**", "/eureka/js/**", "/eureka/images/**")
                .permitAll();

        http.authorizeRequests()
                .antMatchers("/eureka/**").hasRole("EUREKA_CLIENT")
                .and()
                .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(inMemoryUserDetailsManager());
    }

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        inMemoryUserDetailsManager.createUser(User.withUsername("microservice").password("pwd").roles("EUREKA_CLIENT", "ACTUATOR").build());
        return inMemoryUserDetailsManager;
    }

}