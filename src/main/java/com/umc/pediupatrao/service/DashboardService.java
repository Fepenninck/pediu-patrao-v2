package com.umc.pediupatrao.service;

import com.umc.pediupatrao.dto.ResumoDashboardDTO;
import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.entity.StatusPedido;
import com.umc.pediupatrao.repository.PedidoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.Comparator;
import java.time.temporal.ChronoUnit;
import com.umc.pediupatrao.dto.VendaDiariaDTO;
import com.umc.pediupatrao.dto.StatusDashboardDTO;
import com.umc.pediupatrao.dto.ProdutoRankingDTO;
import com.umc.pediupatrao.entity.ItemPedido;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final PedidoRepository pedidoRepository;

    public DashboardService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public ResumoDashboardDTO buscarResumo(
            LocalDate dataInicial,
            LocalDate dataFinal
    ) {
        List<Pedido> pedidos = consultarPeriodo(dataInicial, dataFinal);

        BigDecimal faturamento = new BigDecimal("0.00");
        long pedidosFinalizados = 0;
        long pedidosCancelados = 0;

        for (Pedido pedido : pedidos) {
            if (pedido.getStatus() == StatusPedido.FINALIZADO) {
                faturamento = faturamento.add(valorTotal(pedido));
                pedidosFinalizados++;
            } else if (pedido.getStatus() == StatusPedido.CANCELADO) {
                pedidosCancelados++;
            }
        }

        BigDecimal ticketMedio = new BigDecimal("0.00");
        if (pedidosFinalizados > 0) {
            ticketMedio = faturamento.divide(BigDecimal.valueOf(pedidosFinalizados),
                    2, RoundingMode.HALF_UP);
        }
        return new ResumoDashboardDTO(faturamento, pedidosFinalizados,
                ticketMedio, pedidosCancelados);
    }

    private List<Pedido> consultarPeriodo(LocalDate dataInicial, LocalDate dataFinal) {
        if (dataInicial == null || dataFinal == null) {
            throw new IllegalArgumentException(
                    "A data inicial e a data final são obrigatórias."
            );
        }

        if (dataInicial.isAfter(dataFinal)) {
            throw new IllegalArgumentException(
                    "A data inicial não pode ser posterior à data final."
            );
        }

        if (ChronoUnit.DAYS.between(dataInicial, dataFinal) > 365
                || dataFinal.equals(LocalDate.MAX)) {
            throw new IllegalArgumentException("Consulte no máximo 366 dias por vez.");
        }
        LocalDateTime inicio = dataInicial.atStartOfDay();
        LocalDateTime fimExclusivo = dataFinal.plusDays(1).atStartOfDay();

        return pedidoRepository.buscarPorPeriodo(
                inicio,
                fimExclusivo
        );

    }

    private BigDecimal valorTotal(Pedido pedido) {
        if (pedido.getValorTotal() == null) {
            throw new IllegalStateException("Pedido finalizado sem valor total: " + pedido.getId());
        }
        return pedido.getValorTotal();
    }

    public List<VendaDiariaDTO> buscarVendasDiarias(LocalDate inicio, LocalDate fim) {
        List<Pedido> pedidos = consultarPeriodo(inicio, fim);
        Map<LocalDate, BigDecimal> totais = new LinkedHashMap<>();
        for (long i = 0; i <= ChronoUnit.DAYS.between(inicio, fim); i++) {
            totais.put(inicio.plusDays(i), new BigDecimal("0.00"));
        }
        for (Pedido pedido : pedidos) {
            if (pedido.getStatus() == StatusPedido.FINALIZADO) {
                totais.merge(pedido.getDataHoraEntrada().toLocalDate(), valorTotal(pedido), BigDecimal::add);
            }
        }
        return totais.entrySet().stream()
                .map(e -> new VendaDiariaDTO(e.getKey(), e.getValue())).toList();
    }

    public List<StatusDashboardDTO> buscarStatus(LocalDate inicio, LocalDate fim) {
        List<Pedido> pedidos = consultarPeriodo(inicio, fim);
        return Arrays.stream(StatusPedido.values())
                .map(status -> new StatusDashboardDTO(status,
                        pedidos.stream().filter(p -> p.getStatus() == status).count()))
                .toList();
    }

    public List<ProdutoRankingDTO> buscarProdutos(LocalDate inicio, LocalDate fim, int limite) {
        if (limite < 1 || limite > 50) {
            throw new IllegalArgumentException("O limite deve estar entre 1 e 50.");
        }
        Map<String, ProdutoRankingDTO> ranking = new LinkedHashMap<>();
        for (Pedido pedido : consultarPeriodo(inicio, fim)) {
            if (pedido.getStatus() != StatusPedido.FINALIZADO) continue;
            if (pedido.getItens() == null) {
                throw new IllegalStateException("Pedido finalizado sem itens: " + pedido.getId());
            }
            for (ItemPedido item : pedido.getItens()) {
                if (item.getProdutoId() == null || item.getQuantidade() == null || item.getQuantidade() <= 0) {
                    throw new IllegalStateException("Item inválido no pedido: " + pedido.getId());
                }
                ProdutoRankingDTO anterior = ranking.get(item.getProdutoId());
                long quantidade = item.getQuantidade() + (anterior == null ? 0 : anterior.quantidade());
                ranking.put(item.getProdutoId(), new ProdutoRankingDTO(item.getProdutoId(),
                        item.getNomeProduto(), quantidade));
            }
        }
        return ranking.values().stream()
                .sorted(Comparator.comparingLong(ProdutoRankingDTO::quantidade).reversed()
                        .thenComparing(ProdutoRankingDTO::produtoId))
                .limit(limite).toList();
    }
}
