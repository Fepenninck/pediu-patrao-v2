package com.umc.pediupatrao.config;

import com.umc.pediupatrao.service.UsuarioDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsuarioDetailsService usuarioDetailsService;

    public SecurityConfig(UsuarioDetailsService usuarioDetailsService) {
        this.usuarioDetailsService = usuarioDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/login",
                                "/css/**",
                                "/dist/**",
                                "/plugins/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        // Usuários: somente ADMIN
                        .requestMatchers("/api/usuarios/**", "/usuarios/**")
                        .hasRole("ADMIN")

                        // Clientes: gerente consulta; atendente consulta, cadastra e altera
                        .requestMatchers(HttpMethod.GET,
                                "/api/clientes/**",
                                "/clientes",
                                "/clientes/editar/**")
                        .hasAnyRole("GERENTE", "ATENDENTE")

                        .requestMatchers(
                                "/api/clientes/**",
                                "/clientes/salvar",
                                "/clientes/editar/**")
                        .hasRole("ATENDENTE")

.requestMatchers(HttpMethod.GET, "/api/auditorias/**")
.hasAnyRole("ADMIN", "GERENTE")

.requestMatchers(HttpMethod.POST, "/api/pedidos")
.hasRole("ATENDENTE")

.requestMatchers(HttpMethod.PUT,
        "/api/pedidos/*/desconto",
        "/api/pedidos/*/cancelar")
.hasRole("GERENTE")


.requestMatchers(HttpMethod.PUT, "/api/pedidos/*/status")
.hasAnyRole("ATENDENTE", "GERENTE")


.requestMatchers(HttpMethod.GET, "/api/pedidos/**", "/pedidos")
.authenticated()


.requestMatchers("/api/auditorias/**", "/api/pedidos/**")
.denyAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}