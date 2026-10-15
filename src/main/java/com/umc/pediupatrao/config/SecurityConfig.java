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

            // Página de login e arquivos públicos.
            .requestMatchers(
                "/login",
                "/css/**",
                "/dist/**",
                "/plugins/**",
                "/js/**",
                "/images/**"
            ).permitAll()

            // Administração de usuários: somente ADMIN.
            .requestMatchers(
                "/usuarios",
                "/usuarios/**",
                "/api/usuarios",
                "/api/usuarios/**",
                "/admin"
            ).hasRole("ADMIN")

            // Consulta de auditoria: ADMIN e GERENTE.
            .requestMatchers(
                HttpMethod.GET,
                "/auditorias",
                "/api/auditorias",
                "/api/auditorias/**"
            ).hasAnyRole("ADMIN", "GERENTE")

            // Consulta de clientes: GERENTE e ATENDENTE.
            .requestMatchers(
                HttpMethod.GET,
                "/clientes",
                "/api/clientes",
                "/api/clientes/**"
            ).hasAnyRole("GERENTE", "ATENDENTE")

            // Cadastro, edição e exclusão de clientes: ATENDENTE.
            .requestMatchers(
                "/clientes/novo",
                "/clientes/salvar",
                "/clientes/editar/**",
                "/api/clientes",
                "/api/clientes/**"
            ).hasRole("ATENDENTE")

            // Registro de pedidos: ATENDENTE.
            .requestMatchers(
                HttpMethod.POST,
                "/api/pedidos"
            ).hasRole("ATENDENTE")

            // Desconto e cancelamento: exclusivamente GERENTE.
            .requestMatchers(
                HttpMethod.PUT,
                "/api/pedidos/*/desconto",
                "/api/pedidos/*/cancelar"
            ).hasRole("GERENTE")

            // Avanço do pedido: GERENTE e ATENDENTE.
            .requestMatchers(
                HttpMethod.PUT,
                "/api/pedidos/*/status"
            ).hasAnyRole("GERENTE", "ATENDENTE")

            // Consulta de pedidos: os três perfis.
            .requestMatchers(
                HttpMethod.GET,
                "/pedidos",
                "/api/pedidos",
                "/api/pedidos/**"
            ).hasAnyRole("ADMIN", "GERENTE", "ATENDENTE")

            // Consulta de produtos: os três perfis.
            .requestMatchers(
                HttpMethod.GET,
                "/produtos",
                "/api/produtos",
                "/api/produtos/**"
            ).hasAnyRole("ADMIN", "GERENTE", "ATENDENTE")

            // Manutenção de produtos: ADMIN e GERENTE.
            .requestMatchers(
                "/api/produtos",
                "/api/produtos/**"
            ).hasAnyRole("ADMIN", "GERENTE")

            // Bloqueia operações não previstas nessas APIs.
            .requestMatchers(
                "/api/auditorias",
                "/api/auditorias/**",
                "/api/pedidos",
                "/api/pedidos/**"
            ).denyAll()

            // Página inicial após o login.
            .requestMatchers(HttpMethod.GET, "/")
                .authenticated()

            .anyRequest().denyAll()
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
