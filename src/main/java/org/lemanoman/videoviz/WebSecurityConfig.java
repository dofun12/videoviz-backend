package org.lemanoman.videoviz;

import org.lemanoman.videoviz.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${safemode}")
    private boolean safemode;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        if(safemode){
            http
                    .csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/login", "/static/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin().loginPage("/login").permitAll()
                    .and()
                    .logout().logoutUrl("/logout");
        }else{
            http.csrf().disable().authorizeRequests().antMatchers("/**").permitAll();

        }

    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImp(usuarioRepository);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    ;


}
