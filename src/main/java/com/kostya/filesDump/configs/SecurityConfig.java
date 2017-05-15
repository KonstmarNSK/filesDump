package com.kostya.filesDump.configs;

import com.kostya.filesDump.repositories.interfaces.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

/**
 * Created by Костя on 13.05.2017.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/resources/**", "/register").permitAll()
                .anyRequest().hasRole("USER").and()
                .formLogin().loginPage("/auth").passwordParameter("password").usernameParameter("email").permitAll();
    }

    @Autowired
    UserRepository userRepository;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userRepository);
        auth.authenticationProvider(authenticationProvider);
    }

    @Bean
    public SecurityContextPersistenceFilter getSecurityContextPersistenceFilter(){
        return new SecurityContextPersistenceFilter();
        //needed for saving securityContextHolder between requests (in a session)
    }
}
