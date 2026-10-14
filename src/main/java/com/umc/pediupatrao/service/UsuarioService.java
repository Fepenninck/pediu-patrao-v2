package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.Usuario;
import com.umc.pediupatrao.repository.UsuarioRepository;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Usuario> buscarPorId(String id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public Iterable<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario criarUsuario(Usuario usuario) {
        if (usuario.getUsername() == null || usuario.getUsername().isBlank()) {
            throw new IllegalArgumentException("O nome de usuário é obrigatório.");
        }

        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new IllegalArgumentException("A senha é obrigatória.");
        }

        if (usuario.getRole() == null) {
            throw new IllegalArgumentException("O perfil de acesso é obrigatório.");
        }

        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário com esse nome.");
        }

        usuario.setUsername(usuario.getUsername().trim());
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        return usuarioRepository.save(usuario);
    }

    public Usuario atualizarUsuario(String id, Usuario usuarioAtualizado) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (usuarioAtualizado.getUsername() == null
                || usuarioAtualizado.getUsername().isBlank()) {
            throw new IllegalArgumentException("O nome de usuário é obrigatório.");
        }

        if (usuarioAtualizado.getRole() == null) {
            throw new IllegalArgumentException("O perfil de acesso é obrigatório.");
        }

        String novoUsername = usuarioAtualizado.getUsername().trim();

        usuarioRepository.findByUsername(novoUsername)
                .filter(usuarioComMesmoNome -> !usuarioComMesmoNome.getId().equals(id))
                .ifPresent(usuario -> {
                    throw new IllegalArgumentException(
                            "Já existe um usuário com esse nome."
                    );
                });

        usuarioExistente.setUsername(novoUsername);
        usuarioExistente.setRole(usuarioAtualizado.getRole());

        if (usuarioAtualizado.getPassword() != null
                && !usuarioAtualizado.getPassword().isBlank()) {
            usuarioExistente.setPassword(
                    passwordEncoder.encode(usuarioAtualizado.getPassword())
            );
        }

        return usuarioRepository.save(usuarioExistente);
    }

    public void deletarUsuario(String id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        usuarioRepository.delete(usuario);
    }
}