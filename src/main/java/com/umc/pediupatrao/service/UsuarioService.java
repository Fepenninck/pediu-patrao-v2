package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.Usuario;
import com.umc.pediupatrao.entity.Perfil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
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

    public synchronized Usuario criarUsuario(Usuario usuario) {
    exigirAdministrador();
    if (usuario == null) {
        throw new IllegalArgumentException(
                "Os dados do usuário são obrigatórios."
        );
    }

    if (usuario.getUsername() == null || usuario.getUsername().isBlank()) {
        throw new IllegalArgumentException(
                "O nome de usuário é obrigatório."
        );
    }

    if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
        throw new IllegalArgumentException(
                "A senha é obrigatória."
        );
    }

    if (usuario.getRole() == null) {
        throw new IllegalArgumentException(
                "O perfil de acesso é obrigatório."
        );
    }

    String username = usuario.getUsername().trim();

    if (usuarioRepository.findByUsername(username).isPresent()) {
        throw new IllegalArgumentException(
                "Já existe um usuário com esse nome."
        );
    }

    // Um cadastro novo não pode sobrescrever um usuário existente.
    usuario.setId(null);
    usuario.setUsername(username);
    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

    return usuarioRepository.save(usuario);
}
    public synchronized Usuario atualizarUsuario(String id, Usuario usuarioAtualizado) {
        Usuario autor = exigirAdministrador();
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (usuarioAtualizado == null) {
            throw new IllegalArgumentException("Os dados do usuário são obrigatórios.");
        }
        if (usuarioAtualizado.getUsername() == null
                || usuarioAtualizado.getUsername().isBlank()) {
            throw new IllegalArgumentException("O nome de usuário é obrigatório.");
        }

        if (usuarioAtualizado.getRole() == null) {
            throw new IllegalArgumentException("O perfil de acesso é obrigatório.");
        }

        String novoUsername = usuarioAtualizado.getUsername().trim();

        if (autor.getId().equals(id)) {
            if (usuarioExistente.getRole() != usuarioAtualizado.getRole()) {
                throw new IllegalStateException("Você não pode alterar o próprio perfil.");
            }
            if (!usuarioExistente.getUsername().equals(novoUsername)) {
                throw new IllegalStateException("Para alterar seu login, solicite a outro administrador.");
            }
        }
        if (usuarioExistente.getRole() == Perfil.ADMIN && usuarioAtualizado.getRole() != Perfil.ADMIN) {
            protegerUltimoAdministrador();
        }

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

    public synchronized void deletarUsuario(String id) {
        Usuario autor = exigirAdministrador();
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (usuario.getRole() == Perfil.ADMIN) {
            protegerUltimoAdministrador();
        }
        if (autor.getId().equals(id)) {
            throw new IllegalStateException("Você não pode excluir a própria conta.");
        }
        usuarioRepository.delete(usuario);
    }

    private void protegerUltimoAdministrador() {
        if (usuarioRepository.countByRole(Perfil.ADMIN) <= 1) {
            throw new IllegalStateException("Não é permitido remover ou rebaixar o último administrador.");
        }
    }

    private Usuario exigirAdministrador() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("É necessário autenticar-se como administrador.");
        }
        return usuarioRepository.findByUsername(authentication.getName())
                .filter(usuario -> usuario.getRole() == Perfil.ADMIN)
                .orElseThrow(() -> new AccessDeniedException("A conta não possui permissão de administrador. Entre novamente."));
    }
}
