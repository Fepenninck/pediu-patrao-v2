package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.Perfil;
import com.umc.pediupatrao.entity.Usuario;
import com.umc.pediupatrao.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {
    private UsuarioRepository repository;
    private PasswordEncoder encoder;
    private UsuarioService service;

    private Usuario usuario(String id, String nome, Perfil perfil) {
        Usuario u = new Usuario();
        u.setId(id); u.setUsername(nome); u.setRole(perfil); u.setPassword("hash-anterior");
        return u;
    }

    @BeforeEach void preparar() {
        repository = mock(UsuarioRepository.class);
        encoder = mock(PasswordEncoder.class);
        service = new UsuarioService(repository, encoder);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", "", java.util.List.of()));
        when(repository.findByUsername("admin")).thenReturn(Optional.of(usuario("a", "admin", Perfil.ADMIN)));
    }

    @AfterEach void limpar() { SecurityContextHolder.clearContext(); }

    @Test void naoPermiteAlterarProprioPerfilMesmoComOutrosAdmins() {
        Usuario atual = usuario("a", "admin", Perfil.ADMIN);
        when(repository.findById("a")).thenReturn(Optional.of(atual));
        assertThrows(IllegalStateException.class, () -> service.atualizarUsuario("a", usuario("a", "admin", Perfil.GERENTE)));
        assertEquals(Perfil.ADMIN, atual.getRole());
        verify(repository, never()).save(any());
    }

    @Test void naoPermiteRenomearPropriaContaEContornarIdentificacaoDaSessao() {
        when(repository.findById("a")).thenReturn(Optional.of(usuario("a", "admin", Perfil.ADMIN)));
        assertThrows(IllegalStateException.class, () -> service.atualizarUsuario("a", usuario("a", "outro-login", Perfil.ADMIN)));
        verify(repository, never()).save(any());
    }

    @Test void permiteAlterarPropriaSenhaSemAlterarPerfil() {
        Usuario atual = usuario("a", "admin", Perfil.ADMIN);
        when(repository.findById("a")).thenReturn(Optional.of(atual));
        when(encoder.encode("nova-senha")).thenReturn("hash-novo");
        Usuario alteracao = usuario("a", "admin", Perfil.ADMIN);
        alteracao.setPassword("nova-senha");
        service.atualizarUsuario("a", alteracao);
        assertEquals("hash-novo", atual.getPassword());
        verify(repository).save(atual);
    }

    @Test void bloqueiaExclusaoDoUltimoAdministrador() {
        when(repository.findById("b")).thenReturn(Optional.of(usuario("b", "outro", Perfil.ADMIN)));
        when(repository.countByRole(Perfil.ADMIN)).thenReturn(1L);
        assertThrows(IllegalStateException.class, () -> service.deletarUsuario("b"));
        verify(repository, never()).delete(any());
    }

    @Test void bloqueiaRebaixamentoDoUltimoAdministrador() {
        Usuario atual = usuario("b", "outro", Perfil.ADMIN);
        when(repository.findById("b")).thenReturn(Optional.of(atual));
        when(repository.countByRole(Perfil.ADMIN)).thenReturn(1L);
        assertThrows(IllegalStateException.class, () -> service.atualizarUsuario("b", usuario("b", "outro", Perfil.ATENDENTE)));
        assertEquals(Perfil.ADMIN, atual.getRole());
        verify(repository, never()).save(any());
    }

    @Test void bloqueiaAutoexclusaoMesmoHavendoDoisAdministradores() {
        when(repository.findById("a")).thenReturn(Optional.of(usuario("a", "admin", Perfil.ADMIN)));
        when(repository.countByRole(Perfil.ADMIN)).thenReturn(2L);
        assertThrows(IllegalStateException.class, () -> service.deletarUsuario("a"));
        verify(repository, never()).delete(any());
    }

    @Test void permiteExcluirOutroAdministradorSeRestarUm() {
        Usuario outro = usuario("b", "outro", Perfil.ADMIN);
        when(repository.findById("b")).thenReturn(Optional.of(outro));
        when(repository.countByRole(Perfil.ADMIN)).thenReturn(2L);
        service.deletarUsuario("b");
        verify(repository).delete(outro);
    }

    @Test void permiteRebaixarOutroAdministradorSeRestarUmEPreservaSenhaVazia() {
        Usuario outro = usuario("b", "outro", Perfil.ADMIN);
        Usuario alteracao = usuario("b", "outro", Perfil.GERENTE);
        alteracao.setPassword("");
        when(repository.findById("b")).thenReturn(Optional.of(outro));
        when(repository.countByRole(Perfil.ADMIN)).thenReturn(2L);
        service.atualizarUsuario("b", alteracao);
        assertEquals(Perfil.GERENTE, outro.getRole());
        assertEquals("hash-anterior", outro.getPassword());
        verify(repository).save(outro);
    }

    @Test void sessaoAntigaDeAdministradorRebaixadoNaoPermiteMutacoes() {
        when(repository.findByUsername("admin")).thenReturn(Optional.of(usuario("a", "admin", Perfil.GERENTE)));
        assertThrows(AccessDeniedException.class, () -> service.deletarUsuario("b"));
        assertThrows(AccessDeniedException.class, () -> service.criarUsuario(usuario(null, "novo", Perfil.ADMIN)));
        verify(repository, never()).delete(any());
        verify(repository, never()).save(any());
    }

    @Test void novoCadastroNaoSobrescreveIdEUsaHash() {
        Usuario novo = usuario("id-injetado", " novo ", Perfil.ATENDENTE);
        when(encoder.encode("hash-anterior")).thenReturn("hash-seguro");
        service.criarUsuario(novo);
        assertNull(novo.getId());
        assertEquals("novo", novo.getUsername());
        assertEquals("hash-seguro", novo.getPassword());
        verify(repository).save(novo);
    }

    @Test void loginDuplicadoNaoAlteraUsuario() {
        Usuario outro = usuario("b", "outro", Perfil.GERENTE);
        when(repository.findById("b")).thenReturn(Optional.of(outro));
        assertThrows(IllegalArgumentException.class, () -> service.atualizarUsuario("b", usuario("b", "admin", Perfil.GERENTE)));
        assertEquals("outro", outro.getUsername());
        verify(repository, never()).save(any());
    }
}
