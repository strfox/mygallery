/*
 * Copyright (C) 2019 Michel Faria
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package io.github.michelfaria.mygallery.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import static java.util.Objects.requireNonNull;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyGalleryProperties myGalleryProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if (useAuthentication()) {
            // @formatter:off
            http.authorizeRequests()
                    .anyRequest()
                        .authenticated()
                        .and()
                    .formLogin()
                        .permitAll()
                        .and()
                    .logout()
                        .permitAll();
            // @formatter:on
        } else {
            // @formatter:off
            http.authorizeRequests()
                    .anyRequest()
                        .permitAll()
                        .and()
                    .httpBasic();
            // @formatter:on
        }
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        if (useAuthentication()) {
            UserDetails user =
                    User.withDefaultPasswordEncoder()
                            .username(requireNonNull(myGalleryProperties.getUsername()))
                            .password(requireNonNull(myGalleryProperties.getPassword()))
                            .roles("ADMIN")
                            .build();
            return new InMemoryUserDetailsManager(user);
        } else {
            return super.userDetailsService();
        }
    }

    private boolean useAuthentication() {
        return myGalleryProperties.getUsername() != null && myGalleryProperties.getPassword() != null;
    }
}
