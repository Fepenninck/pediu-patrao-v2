package com.umc.pediupatrao.dto;

import java.math.BigDecimal;

public record ResumoDashboardDTO(
        BigDecimal faturamento,
        long pedidosFinalizados,
        BigDecimal ticketMedio,
        long pedidosCancelados
) {
}
