package com.umc.pediupatrao.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DescontoRequest {

    private BigDecimal percentual;
}