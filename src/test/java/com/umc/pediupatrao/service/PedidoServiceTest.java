package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.*;
import com.umc.pediupatrao.dto.*;
import com.umc.pediupatrao.repository.*;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedidoServiceTest {
    PedidoRepository pedidos;
    ClienteRepository clientes;
    ProdutoRepository produtos;
    AuditoriaService auditoria;
    PedidoService service;

    @BeforeEach void preparar() {
        pedidos = mock(PedidoRepository.class); clientes = mock(ClienteRepository.class);
        produtos = mock(ProdutoRepository.class); auditoria = mock(AuditoriaService.class);
        service = new PedidoService(pedidos, clientes, produtos, auditoria);
        when(pedidos.save(any())).thenAnswer(call -> call.getArgument(0));
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("operador", ""));
    }
    @AfterEach void limpar() { SecurityContextHolder.clearContext(); }

    private Pedido pedido(StatusPedido status) {
        Pedido p = new Pedido(); p.setId("p1"); p.setStatus(status);
        p.setSubtotal(new BigDecimal("100.00")); p.setValorTotal(new BigDecimal("100.00"));
        when(pedidos.findById("p1")).thenReturn(Optional.of(p));
        return p;
    }

    @Test void descontoDeVintePorCentoCalculaValorSeguro() {
        Pedido p = pedido(StatusPedido.RECEBIDO);
        service.aplicarDesconto("p1", new BigDecimal("20"));
        assertEquals(new BigDecimal("20.00"), p.getValorDesconto());
        assertEquals(new BigDecimal("80.00"), p.getValorTotal());
        verify(pedidos).save(p);
    }

    @Test void descontoAcimaDoLimiteOuNegativoNaoGrava() {
        assertThrows(IllegalArgumentException.class, () -> service.aplicarDesconto("p1", new BigDecimal("20.01")));
        assertThrows(IllegalArgumentException.class, () -> service.aplicarDesconto("p1", new BigDecimal("-1")));
        verify(pedidos, never()).save(any());
    }

    @Test void naoPermitePularEtapasNemAlterarPedidoFinalizado() {
        pedido(StatusPedido.RECEBIDO);
        assertThrows(IllegalStateException.class, () -> service.atualizarStatus("p1", StatusPedido.FINALIZADO));
        pedido(StatusPedido.FINALIZADO);
        assertThrows(IllegalStateException.class, () -> service.atualizarStatus("p1", StatusPedido.RECEBIDO));
        verify(pedidos, never()).save(any());
    }

    @Test void transicaoPermitidaGravaStatusEAuditoria() {
        Pedido p = pedido(StatusPedido.RECEBIDO);
        service.atualizarStatus("p1", StatusPedido.EM_PREPARACAO);
        assertEquals(StatusPedido.EM_PREPARACAO, p.getStatus());
        verify(auditoria).registrar("ALTERACAO_STATUS", "PEDIDO", "p1", "status", "RECEBIDO", "EM_PREPARACAO", null);
    }

    @Test void cancelamentoExigeJustificativaERegistraResponsavel() {
        Pedido p = pedido(StatusPedido.RECEBIDO);
        assertThrows(IllegalArgumentException.class, () -> service.cancelarPedido("p1", " "));
        service.cancelarPedido("p1", "  Cliente desistiu  ");
        assertEquals(StatusPedido.CANCELADO, p.getStatus());
        assertEquals("Cliente desistiu", p.getJustificativaCancelamento());
        assertEquals("operador", p.getResponsavelCancelamento());
        assertNotNull(p.getDataHoraCancelamento());
        verify(auditoria).registrar("CANCELAMENTO", "PEDIDO", "p1", "status", "RECEBIDO", "CANCELADO", "Cliente desistiu");
    }

    @Test void pedidoQueJaSaiuNaoPodeSerCanceladoOuReceberDesconto() {
        pedido(StatusPedido.SAIU_PARA_ENTREGA);
        assertThrows(IllegalStateException.class, () -> service.cancelarPedido("p1", "Desistência"));
        assertThrows(IllegalStateException.class, () -> service.aplicarDesconto("p1", BigDecimal.TEN));
        verify(pedidos, never()).save(any());
    }

    @Test void pedidoComMultiplosItensCalculaTotaisPeloCadastro() {
        when(clientes.findById("c1")).thenReturn(Optional.of(new Cliente()));
        Produto a = new Produto(); a.setId("a"); a.setNome("Pizza"); a.setPreco(19.90); a.setAtivo(true);
        Produto b = new Produto(); b.setId("b"); b.setNome("Suco"); b.setPreco(5.50); b.setAtivo(true);
        when(produtos.findById("a")).thenReturn(Optional.of(a));
        when(produtos.findById("b")).thenReturn(Optional.of(b));
        ItemPedidoRequest ia = new ItemPedidoRequest(); ia.setProdutoId("a"); ia.setQuantidade(2);
        ItemPedidoRequest ib = new ItemPedidoRequest(); ib.setProdutoId("b"); ib.setQuantidade(1);
        CriarPedidoRequest request = new CriarPedidoRequest(); request.setClienteId("c1"); request.setItens(List.of(ia, ib));
        Pedido resultado = service.criarPedido(request);
        assertEquals(2, resultado.getItens().size());
        assertEquals(new BigDecimal("45.30"), resultado.getValorTotal());
        assertEquals(StatusPedido.RECEBIDO, resultado.getStatus());
    }
}
