package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.*;
import com.umc.pediupatrao.repository.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.mockito.ArgumentCaptor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuditoriaServiceTest {
    @AfterEach void limpar() { SecurityContextHolder.clearContext(); }

    @Test void persisteExclusaoComResponsavelPerfilHorarioEValores() {
        AuditoriaRepository auditorias = mock(AuditoriaRepository.class);
        UsuarioRepository usuarios = mock(UsuarioRepository.class);
        Usuario usuario = new Usuario(); usuario.setUsername("gerente"); usuario.setRole(Perfil.GERENTE);
        when(usuarios.findByUsername("gerente")).thenReturn(Optional.of(usuario));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("gerente", ""));
        new AuditoriaService(auditorias, usuarios).registrar("EXCLUSAO", "CLIENTE", "c1", "cliente", "Maria", null, null);
        var captor = ArgumentCaptor.forClass(Auditoria.class);
        verify(auditorias).save(captor.capture());
        Auditoria registro = captor.getValue();
        assertEquals("gerente", registro.getUsuario());
        assertEquals(Perfil.GERENTE, registro.getPerfil());
        assertNotNull(registro.getDataHora());
        assertEquals("EXCLUSAO", registro.getOperacao());
        assertEquals("CLIENTE", registro.getEntidade());
        assertEquals("c1", registro.getEntidadeId());
        assertEquals("Maria", registro.getValorAnterior());
        assertNull(registro.getValorNovo());
    }
}
