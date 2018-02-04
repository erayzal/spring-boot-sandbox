package com.erayzal.springbootsandbox.adminserver;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Originally copied from :
        //    https://github.com/codecentric/spring-boot-admin/tree/1.5.x/spring-boot-admin-samples/spring-boot-admin-sample

        // Page with login form is served as /login.html and does a POST on /login
        http.formLogin().loginPage("/login.html").loginProcessingUrl("/login").permitAll();
        // The UI does a POST on /logout on logout
        http.logout().logoutUrl("/logout");
        // The ui currently doesn't support csrf
        http.csrf().disable();

        // Requests for the login page and the static assets are allowed
        http.authorizeRequests()
                .antMatchers("/login.html", "/**/*.css", "/img/**", "/third-party/**")
                .permitAll();

        // Modifying stuff is only allowed for admins
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST,"/api/applications/**").hasRole("ADMIN");
        http.authorizeRequests()
                .antMatchers("/api/applications/**/heapdump").hasRole("ADMIN");

        // ... and any other request needs to be authorized
        http.authorizeRequests().antMatchers("/**").authenticated();

        // Enable so that the clients can authenticate via HTTP basic for registering
        http.httpBasic();
    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(inMemoryUserDetailsManager());
//    }
//
//    @Bean
//    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
//        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
//        inMemoryUserDetailsManager.createUser(User.withUsername("microservice").password("pwd").roles("EUREKA_CLIENT", "ACTUATOR").build());
//        return inMemoryUserDetailsManager;
//    }

}