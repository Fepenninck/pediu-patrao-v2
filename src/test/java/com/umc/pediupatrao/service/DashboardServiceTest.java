package com.umc.pediupatrao.service;

import com.umc.pediupatrao.entity.*;
import com.umc.pediupatrao.repository.PedidoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DashboardServiceTest {
    private final LocalDate inicio = LocalDate.of(2026, 11, 1);
    private final LocalDate fim = inicio.plusDays(2);
    private PedidoRepository repository;
    private DashboardService service;

    @BeforeEach void preparar() {
        repository = mock(PedidoRepository.class);
        service = new DashboardService(repository);
    }

    private Pedido pedido(StatusPedido status, String valor, int dia) {
        Pedido p = new Pedido();
        p.setStatus(status);
        p.setValorTotal(new BigDecimal(valor));
        p.setDataHoraEntrada(inicio.plusDays(dia).atTime(15, 30));
        return p;
    }

    private void dados(Pedido... pedidos) {
        when(repository.buscarPorPeriodo(inicio.atStartOfDay(), fim.plusDays(1).atStartOfDay()))
                .thenReturn(List.of(pedidos));
    }

    @Test void resumoConsideraSomenteFinalizadosEArredondaTicket() {
        dados(pedido(StatusPedido.FINALIZADO, "10.00", 0),
                pedido(StatusPedido.FINALIZADO, "10.01", 1),
                pedido(StatusPedido.CANCELADO, "100.00", 0),
                pedido(StatusPedido.PRONTO, "200.00", 0));
        var resumo = service.buscarResumo(inicio, fim);
        assertEquals(new BigDecimal("20.01"), resumo.faturamento());
        assertEquals(new BigDecimal("10.01"), resumo.ticketMedio());
        assertEquals(2, resumo.pedidosFinalizados());
        assertEquals(1, resumo.pedidosCancelados());
    }

    @Test void periodoVazioNaoDividePorZero() {
        dados();
        assertEquals(new BigDecimal("0.00"), service.buscarResumo(inicio, fim).ticketMedio());
    }

    @Test void vendasIncluemDiasSemMovimentoEmOrdem() {
        dados(pedido(StatusPedido.FINALIZADO, "30.00", 1),
                pedido(StatusPedido.CANCELADO, "90.00", 1));
        var vendas = service.buscarVendasDiarias(inicio, fim);
        assertEquals(3, vendas.size());
        assertEquals(inicio, vendas.getFirst().data());
        assertEquals(new BigDecimal("0.00"), vendas.getFirst().faturamento());
        assertEquals(new BigDecimal("30.00"), vendas.get(1).faturamento());
        verify(repository).buscarPorPeriodo(inicio.atStartOfDay(), fim.plusDays(1).atStartOfDay());
    }

    @Test void statusIncluiTodasAsSituacoes() {
        dados(pedido(StatusPedido.RECEBIDO, "10", 0));
        var status = service.buscarStatus(inicio, fim);
        assertEquals(StatusPedido.values().length, status.size());
        assertEquals(1, status.stream().filter(s -> s.status() == StatusPedido.RECEBIDO).findFirst().orElseThrow().quantidade());
    }

    @Test void rankingSomaQuantidadePorProdutoEAplicaLimite() {
        ItemPedido pizza = new ItemPedido();
        pizza.setProdutoId("pizza"); pizza.setNomeProduto("Pizza"); pizza.setQuantidade(2);
        ItemPedido suco = new ItemPedido();
        suco.setProdutoId("suco"); suco.setNomeProduto("Suco"); suco.setQuantidade(3);
        Pedido a = pedido(StatusPedido.FINALIZADO, "10", 0);
        a.setItens(List.of(pizza, suco));
        Pedido b = pedido(StatusPedido.FINALIZADO, "10", 1);
        b.setItens(List.of(pizza));
        dados(a, b);
        var ranking = service.buscarProdutos(inicio, fim, 1);
        assertEquals(1, ranking.size());
        assertEquals("pizza", ranking.getFirst().produtoId());
        assertEquals(4, ranking.getFirst().quantidade());
    }

    @Test void rejeitaFiltrosInvalidosAntesDeConsultarBanco() {
        assertThrows(IllegalArgumentException.class, () -> service.buscarResumo(fim, inicio));
        assertThrows(IllegalArgumentException.class, () -> service.buscarResumo(null, fim));
        assertThrows(IllegalArgumentException.class, () -> service.buscarResumo(inicio, inicio.plusDays(366)));
        assertThrows(IllegalArgumentException.class, () -> service.buscarProdutos(inicio, fim, 0));
        verifyNoInteractions(repository);
    }

    @Test void dadosMonetariosIncompletosNaoViramFaturamentoZero() {
        Pedido p = pedido(StatusPedido.FINALIZADO, "10", 0);
        p.setValorTotal(null);
        dados(p);
        assertThrows(IllegalStateException.class, () -> service.buscarResumo(inicio, fim));
    }
}
