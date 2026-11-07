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

        LocalDateTime inicio = dataInicial.atStartOfDay();
        LocalDateTime fimExclusivo = dataFinal.plusDays(1).atStartOfDay();

        List<Pedido> pedidos = pedidoRepository.buscarPorPeriodo(
                inicio,
                fimExclusivo
        );

        BigDecimal faturamento = new BigDecimal("0.00");
        long pedidosFinalizados = 0;
        long pedidosCancelados = 0;

        for (Pedido pedido : pedidos) {
            if (pedido.getStatus() == StatusPedido.FINALIZADO) {
                faturamento = faturamento.add(pedido.getValorTotal());
                pedidosFinalizados++;
            } else if (pedido.getStatus() == StatusPedido.CANCELADO) {
                pedidosCancelados++;
            }
        }

        BigDecimal ticketMedio = new BigDecimal("0.00");

        if (pedidosFinalizados > 0) {
            ticketMedio = faturamento.divide(
                    BigDecimal.valueOf(pedidosFinalizados),
                    2,
                    RoundingMode.HALF_UP
            );
        }

        return new ResumoDashboardDTO(
                faturamento,
                pedidosFinalizados,
                ticketMedio,
                pedidosCancelados
        );
    }
}