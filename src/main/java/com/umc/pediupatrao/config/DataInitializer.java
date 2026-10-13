package com.umc.pediupatrao.config;

import com.umc.pediupatrao.entity.Perfil;
import com.umc.pediupatrao.entity.Usuario;
import com.umc.pediupatrao.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository repo) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            criarUsuarioSeNaoExistir(repo, encoder, "admin", "teste123", Perfil.ADMIN);
            criarUsuarioSeNaoExistir(repo, encoder, "gerente", "teste123", Perfil.GERENTE);
            criarUsuarioSeNaoExistir(repo, encoder, "atendente", "teste123", Perfil.ATENDENTE);
        };
    }

    private void criarUsuarioSeNaoExistir(
            UsuarioRepository repo,
            BCryptPasswordEncoder encoder,
            String username,
            String senha,
            Perfil perfil) {

        repo.findByUsername(username).ifPresentOrElse(usuario -> {
            if (usuario.getRole() == null) {
                usuario.setRole(perfil);
                repo.save(usuario);
            }
        }, () -> {
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPassword(encoder.encode(senha));
            usuario.setRole(perfil);
            repo.save(usuario);
        });
    }
}