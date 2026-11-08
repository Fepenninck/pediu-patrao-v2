package com.umc.pediupatrao.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VendaDiariaDTO(LocalDate data, BigDecimal faturamento) {}
