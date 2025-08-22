package cn.timflux.storyseek.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
/**
 * ClassName: SecurityConfig
 * Package: cn.timflux.storyseek.common.config
 * Description:
 *
 * @Author 一剑霜寒十四州
 * @Create 2025/8/7 下午10:56
 * @Version 1.0
 */
@Configuration
public class SecurityConfig {

    @Value("${monitor.username}") private String username;
    @Value("${monitor.password}") private String password;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/**").authenticated()  // 只保护 actuator
                .anyRequest().permitAll()  // 其他接口不认证
            )
            .httpBasic(Customizer.withDefaults()) // Basic Auth
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User
            .withUsername(username)
            .password(password) // password 是配置中已加密的
            .roles("MONITOR")
            .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
