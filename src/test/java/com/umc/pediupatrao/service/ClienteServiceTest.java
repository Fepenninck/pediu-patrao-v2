package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.Cliente;
import com.umc.pediupatrao.repository.ClienteRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClienteServiceTest {
    @Test void exclusaoRegistraIdENomeAnteriorNaAuditoria() {
        ClienteRepository repository = mock(ClienteRepository.class);
        AuditoriaService auditoria = mock(AuditoriaService.class);
        Cliente cliente = new Cliente(); cliente.setId("c1"); cliente.setNome("Maria");
        when(repository.findById("c1")).thenReturn(Optional.of(cliente));
        new ClienteService(repository, auditoria).excluir("c1");
        var ordem = inOrder(repository, auditoria);
        ordem.verify(repository).delete(cliente);
        ordem.verify(auditoria).registrar("EXCLUSAO", "CLIENTE", "c1", "cliente", "Cliente excluído: Maria", null, null);
    }

    @Test void clienteInexistenteNaoGeraAuditoriaDeExclusao() {
        ClienteRepository repository = mock(ClienteRepository.class);
        AuditoriaService auditoria = mock(AuditoriaService.class);
        assertThrows(IllegalArgumentException.class, () -> new ClienteService(repository, auditoria).excluir("ausente"));
        verify(repository, never()).delete(any());
        verifyNoInteractions(auditoria);
    }

    @Test void falhaAoExcluirNaoRegistraSucesso() {
        ClienteRepository repository = mock(ClienteRepository.class);
        AuditoriaService auditoria = mock(AuditoriaService.class);
        Cliente cliente = new Cliente(); cliente.setId("c1");
        when(repository.findById("c1")).thenReturn(Optional.of(cliente));
        doThrow(new IllegalStateException("Banco indisponível")).when(repository).delete(cliente);
        assertThrows(IllegalStateException.class, () -> new ClienteService(repository, auditoria).excluir("c1"));
        verifyNoInteractions(auditoria);
    }
}
