package com.umc.pediupatrao.dto;

import com.umc.pediupatrao.entity.StatusPedido;

public record StatusDashboardDTO(StatusPedido status, long quantidade) {}
